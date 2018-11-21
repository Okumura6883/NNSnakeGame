package SnakeGame.trainer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.neuroph.core.NeuralNetwork;
import SnakeGame.SnakeGame;

public class Population extends ArrayList<EvolutionSnake> {

  public static final double MUTATION_RATE = 0.1;
  public static final double MUTATION_RANGE = 0.3;
  public static final int DEFAULT_GENERATIONS = 10000;
  public static final int DEFAULT_SELECT = 4;
  public static final int DEFAULT_POPULATION = 30;
  public static final int DEFAULT_NEURONS = 20;
  private static Random generator = new Random();
  
  private int populationSize;
  private int currentGen;
  
  public double mutationRate = MUTATION_RATE;
  public double mutationRange = MUTATION_RANGE;
  
  public Population(int size) {
    System.out.println("Population Initializing");
    populationSize = size;
    for (int i = 0; i < size; i++) {
      add(EvolutionSnake.createSnake(i));
    }
    evolve();
  }
  
  public Population() {
    this(DEFAULT_POPULATION);
  }
  
  public Population(String dir, int gen) {
    System.out.println("Population Initializing");
    currentGen = gen;
    populationSize = DEFAULT_POPULATION;
    File[] files = new File(SnakeTrainer.NNPATH + "/Gen" + gen + "/").listFiles();
    int count = 0;
    for (File file : files) {
      NeuralNetwork nNetwork = NeuralNetwork.createFromFile(file);
      add(EvolutionSnake.copyFromNetwork(nNetwork, count++));
    }
    naturalSelection();
    evolve(DEFAULT_GENERATIONS - gen);
  }
  
  public ArrayList<EvolutionSnake> selectBestSnakes(int num) {
    
    Collections.sort(this);
    ArrayList<EvolutionSnake> bestSnakes = new ArrayList<EvolutionSnake>();
    for (int i = 0; i < num && i < size(); i++) {
      bestSnakes.add(get(i));
    }
    return bestSnakes;
  }
  
  public ArrayList<EvolutionSnake> selectBestSnakes() {
    return selectBestSnakes(DEFAULT_SELECT);
  }
  
  public void mutate(ArrayList<EvolutionSnake> snakes) {
    mutate(snakes, mutationRate, mutationRange);
  }
  
  public void mutate(ArrayList<EvolutionSnake> snakes, double rate, double range) {
    for (EvolutionSnake snake: snakes) {
      snake.mutate(rate, range);
    }
  }
  
  public double getAverageScore() {
    double sum = 0;
    for (EvolutionSnake snake : this) {
      sum += snake.score;
    }
    return sum / this.size();
  }
  
  public void saveProgress() {
    int count = 0;
    for (EvolutionSnake snake : selectBestSnakes()) {
      new File(SnakeTrainer.NNPATH + "Gen" + currentGen).mkdirs();
      snake.newNetwork.save(SnakeTrainer.NNPATH + "Gen" + currentGen + "/Parent" + ++count + ".nnet");
    }
  }
  
  public double getBestScore() {
    double sum = 0;
    int count = 0;
    for (EvolutionSnake snake : selectBestSnakes()) {
      sum += snake.score;
      count++;
    }
    return sum / count;
  }
  
  public double getAverageFruitEaten() {
    double sum = 0;
    int count = 0;
    int times = 1000;
    for (EvolutionSnake snake : this) {
      sum += snake.totalFruit;
      count++;
      times = snake.times;
    }
    return sum / (count * times);
  }
  
  public double getBestFruitEaten() {
    double sum = 0;
    int count = 0;
    int times = 1000;
    for (EvolutionSnake snake : selectBestSnakes()) {
      sum += snake.totalFruit;
      count++;
      times = snake.times;
    }
    return sum / (count * times);
  }
  
  public int getMostFruitEaten() {
    int most = 0;
    for (EvolutionSnake snake : selectBestSnakes()) {
      most = Math.max(most, snake.snake.game.mostFruit);
    }
    return most;
  }
  
  public int getMostStep() {
    int most = 0;
    for (EvolutionSnake snake : selectBestSnakes()) {
      most = Math.max(most, snake.snake.game.mostStep);
    }
    return most;
  }
  
  public EvolutionSnake makeOffspring(ArrayList<EvolutionSnake> snakes) {
    if (snakes.size() < 2) {
      System.out.println("At least 2 parents are required to make new offsrpings");
      System.exit(1);
    }
    EvolutionSnake parent1 = snakes.get(generator.nextInt(snakes.size()));
    EvolutionSnake parent2;
    do {
      parent2 = snakes.get(generator.nextInt(snakes.size()));
      
    } while (parent1.id == parent2.id);
    return parent1.crossover(parent2);
  }
  
  public void play() {
    currentGen++;
    System.out.print("\nPlaying Gen " + currentGen + " ");
    ExecutorService pool = Executors.newWorkStealingPool();
    for (EvolutionSnake snake: this) {
      pool.execute(new SnakeGame(snake, false));
    }
    pool.shutdown();
  }
  
  public void waitForTrainers() {
    boolean done = false;
    while (!done) {
      done = true;
      for (EvolutionSnake snake: this) {
        done = done && snake.done;
      }
      Thread.yield();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    System.out.println();
  }
  
  public void reindex() {
    for (int i = 0; i < size(); i++) {
      get(i).id = i;
    }
  }
  
  public void naturalSelection() {
    
    // System.out.println("Selecting From Gen " + currentGen);
    ArrayList<EvolutionSnake> bestSnakes = selectBestSnakes();
//    for (EvolutionSnake snake : bestSnakes) {
//      printGene(snake.getGenes());
//    }
    ArrayList<EvolutionSnake> offsprings = new ArrayList<EvolutionSnake>();
    
    for (int i = 0; i < populationSize - bestSnakes.size(); i++) {
      offsprings.add(makeOffspring(bestSnakes));
    }
    
    mutate(offsprings);
    
    clear();
    addAll(bestSnakes);
    addAll(offsprings);
    
    reindex();
    resetScore();
  }
  
  public void resetScore() {
    for (EvolutionSnake snake : this) {
      snake.reset();
      snake.done = false;
    } 
  }
  
  public void report() {
    System.out.println("Average Score: " + (int) getAverageScore() 
        + "   Best Scores: " + (int) getBestScore() 
        + "\nAverage Fruit Eaten: " + (int) getAverageFruitEaten() 
        + "   Best Average Fruit Eaten: " + (int) getBestFruitEaten()
        + "\nMost Fruit Eaten: " + getMostFruitEaten()
        + "   Most Step: " + getMostStep());
  }
  
  public void evolve() {
    evolve(DEFAULT_GENERATIONS);
  }
  
  public void evolve(int times) {
    for (int i = 0; i < times; i++) {
      play();
      waitForTrainers();
      report();
      saveProgress();
      naturalSelection();
    }
  }
  
  public String toString() {
    String str = "Population [";
    for (EvolutionSnake snake : this) {
      str += "   \n" + snake;
    }
    str += "]";
    return str;
  }
  
  public static int maxGen;
  
  public static void continueGeneration() {
    List<File> files = Arrays.asList(new File(SnakeTrainer.NNPATH + "/").listFiles());
    List<File> dirs = new ArrayList<File>();
    for (File file : files) {
      if  (file.isDirectory()) {
        dirs.add(file);
      }
    }
    
    dirs.sort(new Comparator<File>(){
      public int compare(File file1, File file2) {
        int a = Integer.parseInt(file1.getName().split("Gen")[1]);
        int b = Integer.parseInt(file2.getName().split("Gen")[1]);
        maxGen = (int) Math.max(maxGen, a);
        maxGen = (int) Math.max(maxGen, b);
        return b - a;
      }
    });
    
    System.out.println("Found Max Generation: Gen" + maxGen);
    
    new Population("Gen", maxGen);
  }
  
  public static void main(String[] args) {
    // new Population();
    continueGeneration();
  }
}
