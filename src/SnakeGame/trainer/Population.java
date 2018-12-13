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
  public static final double MUTATION_RANGE = 0.2;
  public static final int DEFAULT_GENERATIONS = 10000;
  public static final int DEFAULT_SELECT = 10;
  public static final int DEFAULT_POPULATION = 80;
  public static final int DEFAULT_NEURONS = 25;
  public static final int DEFAULT_SAVE = 50;
  private static Random generator = new Random();
  
  private int populationSize;
  private int currentGen;
  private int generations;
  
  public double mutationRate = MUTATION_RATE;
  public double mutationRange = MUTATION_RANGE;
  
  public Population(int size, int generations, boolean newPop) {
    
    System.out.println("Population Initializing\n");
    
    this.populationSize = size;
    if (!newPop) {

      this.generations = generations;
      List<File> files = Arrays.asList(new File(SnakeTrainer.NNPATH + "/").listFiles());
      List<File> dirs = new ArrayList<File>();
      for (File file : files) {
        if  (file.isDirectory()) {
          dirs.add(file);
        }
      }
      
      int maxGen = 0;
      
      for (File file : dirs) {
        maxGen = (int) Math.max(maxGen, Integer.parseInt(file.getName().split("Gen")[1]));
      }
      
      if (maxGen != 0) {
        
        currentGen = maxGen;
        System.out.println("Found Existing Generation: Gen" + maxGen);
        
        files = Arrays.asList(new File(SnakeTrainer.NNPATH + "/Gen" + maxGen + "/").listFiles());
        
        for (File file : files) {
          EvolutionSnake oldSnake = EvolutionSnake.createSnake();
          oldSnake.setNnet(NeuralNetwork.createFromFile(file));
          add(oldSnake);
        }
        this.generations = generations - maxGen;
      } else {
        System.out.println("Generating New Generation: Gen");
      }
    }
    populate();
    reset();
  }
  
  public Population(int size, int generations) {
    this(size, generations, false);
  }
  
  public Population(int size) {
    this(size, DEFAULT_GENERATIONS);
  }
  
  public Population() {
    this(DEFAULT_POPULATION);
  }
  
  public void populate() {
    if (size() == populationSize) {
      System.out.println("Population Already populated");
      return;
    }
    for (int i = size(); i < populationSize; i++) {
      add(EvolutionSnake.createSnake());
    }
  }
  
  public ArrayList<EvolutionSnake> selectBestSnakes() {
    return selectBestSnakes(DEFAULT_SELECT);
  }
  
  public ArrayList<EvolutionSnake> selectBestSnakes(int num) {
    
    Collections.sort(this);
    ArrayList<EvolutionSnake> bestSnakes = new ArrayList<EvolutionSnake>();
    for (int i = 0; i < num && i < size(); i++) {
      bestSnakes.add(get(i));
    }
    return bestSnakes;
  }
  
  public EvolutionSnake mutate(EvolutionSnake snake) {
    return mutate(new ArrayList<EvolutionSnake>(){{add(snake);}}, mutationRate, mutationRange).get(0);
  }
  
  public EvolutionSnake mutate(EvolutionSnake snake, double rate, double range) {
    return mutate(new ArrayList<EvolutionSnake>(){{add(snake);}}, rate, range).get(0);
  }
  
  public ArrayList<EvolutionSnake> mutate(ArrayList<EvolutionSnake> snakes) {
    return mutate(snakes, mutationRate, mutationRange);
  }
  
  public ArrayList<EvolutionSnake> mutate(ArrayList<EvolutionSnake> snakes, double rate, double range) {
    for (EvolutionSnake snake: snakes) {
      snake.mutate(rate, range);
    }
    return snakes;
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
      pool.execute(new SnakeGame(snake));
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
        e.printStackTrace();
      }
    }
    System.out.println();
  }
  
  public void naturalSelection() {
    
    ArrayList<EvolutionSnake> bestSnakes = selectBestSnakes();
    ArrayList<EvolutionSnake> offsprings = new ArrayList<EvolutionSnake>();
    
    for (int i = 0; i < populationSize - bestSnakes.size(); i++) {
      offsprings.add(makeOffspring(bestSnakes));
    }
    
    mutate(offsprings);
    
    clear();
    addAll(bestSnakes);
    addAll(offsprings);
    reset();
  }
  
  /**
   * Clearing some data in Snake trainers
   */
  private void reset() {
    for (int i = 0; i < size(); i++) {
      get(i).id = i;
      get(i).reset();
    }
  }
  
  private void report() {
    System.out.println("Average Score: " + String.format("%.2f" , getAverageScore())
        + "   Best Scores: " + String.format("%.2f" , getBestScore())
        + "\nAverage Fruit Eaten: " + String.format("%.4f" , getAverageFruitEaten()) 
        + "   Best Average Fruit Eaten: " + String.format("%.4f" , getBestFruitEaten())
        + "\nMost Fruit Eaten: " + getMostFruitEaten()
        + "   Most Step: " + getMostStep());
  }
  
  private void simulate() {
    simulate(generations);
  }
  
  private void simulate(int times) {
    for (int i = 0; i < times; i++) {
      play();
      waitForTrainers();
      report();
      saveProgress();
      naturalSelection();
    }
  }
  
  public static void main(String[] args) {
    new Population().simulate();
  }
  
  /**
   * Save the the progress so it can be read later
   */
  public void saveProgress() {
    if (currentGen % DEFAULT_SAVE != 0) {
      return;
    }
    int count = 0;
    for (EvolutionSnake snake : selectBestSnakes()) {
      new File(SnakeTrainer.NNPATH + "Gen" + currentGen).mkdirs();
      snake.save(SnakeTrainer.NNPATH + "Gen" + currentGen + "/Parent" + ++count + ".nnet");
    }
  }
  
  /**
   * Override toString to print
   */
  @Override
  public String toString() {
    String str = "Population [";
    for (EvolutionSnake snake : this) {
      str += "   \n" + snake;
    }
    str += "]";
    return str;
  }
  
  // Some useless statics getter below
  
  public double getAverageScore() {
    double sum = 0;
    for (EvolutionSnake trainer : this) {
      sum += trainer.score;
    }
    return sum / this.size();
  }
  
  public double getBestScore() {
    double sum = 0;
    int count = 0;
    for (EvolutionSnake trainer : selectBestSnakes()) {
      sum += trainer.score;
      count++;
    }
    return sum / count;
  }
  
  public double getAverageFruitEaten() {
    double sum = 0;
    int count = 0;
    int times = 1000;
    for (EvolutionSnake trainer : this) {
      sum += trainer.totalFruit;
      count++;
      times = trainer.times;
    }
    return sum / (count * times);
  }
  
  public double getBestFruitEaten() {
    double sum = 0;
    int count = 0;
    int times = 1000;
    for (EvolutionSnake trainer : selectBestSnakes()) {
      sum += trainer.totalFruit;
      count++;
      times = trainer.times;
    }
    return sum / (count * times);
  }
  
  public int getMostFruitEaten() {
    int most = 0;
    for (EvolutionSnake trainer : selectBestSnakes()) {
      most = Math.max(most, trainer.game.mostFruit);
    }
    return most;
  }
  
  public int getMostStep() {
    int most = 0;
    for (EvolutionSnake trainer : selectBestSnakes()) {
      most = Math.max(most, trainer.game.mostStep);
    }
    return most;
  }
}
