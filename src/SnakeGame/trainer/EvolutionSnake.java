package SnakeGame.trainer;

import java.util.Random;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.WeightsRandomizer;
import SnakeGame.util.*;

public class EvolutionSnake extends SnakeTrainer implements Comparable<EvolutionSnake>{

  public static final String NAME = "EvoSnake";
  public static final int DEFAULT_LIMIT = 500;
  public static final int DEFAULT_TEST = 1000;
  private static Random generator = new Random();
  int id;
  double score;
  int death;
  int totalFruit;
  
  private EvolutionSnake(int times, int limit, String base, int id, int neurons) {
    super(NAME, base, TEST, times, limit, neurons);
    this.id = id;
  }
  
  public static EvolutionSnake createSnake(int id) {
    return createSnake(id, Population.DEFAULT_NEURONS);
  }

  public static EvolutionSnake createSnake(int id, int neurons) {
    return createSnake(DEFAULT_TEST, id, neurons);
  }
  
  public static EvolutionSnake createSnake(int times, int id, int neurons) {
    return createSnake(times, DEFAULT_LIMIT, id, neurons);
  }
  
  public static EvolutionSnake createSnake(int times, int limit, int id, int neurons) {
    return new EvolutionSnake(times, limit, NAME, id, neurons);
  }
  
  public void runTest() {
    
    int leftBlocked = snake.hasObstacle(Direction.LEFT);
    int frontBlocked = snake.hasObstacle(Direction.UP);
    int rightBlocked = snake.hasObstacle(Direction.RIGHT);
    
    int choice = 0;
    double reward = -1;
    
    for (int i = -1; i <= 1; i++) {
      newNetwork.setInput(leftBlocked, frontBlocked, rightBlocked, angleToFruit(), i);
      newNetwork.calculate();
      double result = newNetwork.getOutput()[0];
      // System.out.println("Possible turn: " + i + " Result: " + result);
      if (result > reward) {
        reward = result;
        choice = i;
      }
    }
    // System.out.println("Computer choose: " + choice);
    if (choice == -1) {
      snake.setDirection(snake.dir.toLeft());
    }
    if (choice == 1) {
      snake.setDirection(snake.dir.toRight());
    }
  }
  
  public void setupBaseNetwork() {
    
  }
  
  public void recordDeath() {
    death++;
    score -= 1000 / (times * limit);
  }
  
  public void recordFruit() {
    score += 1000 * Math.pow(1.2, snake.game.fruitEaten) / times;
  }
  
  public void validateMove() {
    score -= 100 / (times * limit);
  }

  public void runLearn() {
    System.out.println("Evolution Snake doesn't learn!");
    System.exit(1);
  }
  
  public EvolutionSnake copyFromGenes(double[] genes) {
    EvolutionSnake copy = new EvolutionSnake(times, limit, base, id, neurons);
    copy.newNetwork.setWeights(genes);
    return copy;
  }
  
  public static EvolutionSnake copyFromNetwork(NeuralNetwork nnet, int id) {
    EvolutionSnake copy = new EvolutionSnake(DEFAULT_TEST, DEFAULT_LIMIT, 
        NAME, id, Population.DEFAULT_NEURONS);
    copy.newNetwork = nnet;
    return copy;
  }

  public EvolutionSnake crossover(EvolutionSnake mate) {
    Double[] genes = newNetwork.getWeights();
    Double[] mateGenes = mate.getGenes();
    
    double[] newGenes = new double[genes.length];
    
    for (int i = 0; i < genes.length; i++) {
      if (generator.nextDouble() >= 0.5) {
        newGenes[i] = genes[i];
      } else {
        newGenes[i] = mateGenes[i];
      }
    }
    return copyFromGenes(newGenes);    
  }
  
  public Double[] getGenes() {
    return newNetwork.getWeights();
  }
  
  public void setupNewNetwork() {
    
    newNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 5, neurons, 1);
    
    newNetwork.randomizeWeights(new WeightsRandomizer(new Random()));
    
    BackPropagation lr = new BackPropagation();
    
    lr.addListener(this);
    lr.setMaxIterations(10);
    lr.setLearningRate(0.1);
    lr.setMaxError(0.025);
    newNetwork.setLearningRule(lr);
    
    data = new DataSet(5, 1);
  }
  
  public void reset() {
    score = 0;
    death = 0;
    totalFruit = 0;
  }
  
  public void learn() {
    totalFruit += snake.game.totalFruit;
    // super.learn();
    // System.out.println(NAME + id + " Score: " + (int) score);
    // super.learn();
    // System.out.println("Closer moves: " + closer);
    // System.out.println("Farther moves: " + far);
  }
  
  private double angleToFruit() {
    
    FruitLoop fruit = snake.game.fruit;
    
    int dx = snake.getHead().getX() - fruit.getX();
    int dy = snake.getHead().getY() - fruit.getY();

    double angle = 0;
    
    if (dx == 0) {
      angle = -Math.signum(dy) * Math.PI / 2;
    } else {
      angle = -Math.atan2(dy, dx);
    }
    angle += Direction.DIRECTIONS.indexOf(snake.dir) * Math.PI / 2;
    if (angle > Math.PI) {
      angle -= 2 * Math.PI;
    }
    
    return angle / Math.PI;
  }
  
//  private double distanceToFruit() {
//    FruitLoop fruit = snake.game.fruit;
//    double dx = snake.getHead().getX() - fruit.getX();
//    double dy = snake.getHead().getY() - fruit.getY();
//    return Math.sqrt(dx * dx + dy * dy);
//  }
  
  public void handleLearningEvent(LearningEvent event) {
    BackPropagation lr = (BackPropagation)event.getSource();
    if(lr.isStopped()) {
      System.out.println(NAME + id + " NetError: " + lr.getTotalNetworkError());
    }
  }
  
  public void mutate(double rate, double range) {
    Double[] genes = newNetwork.getWeights();
    double[] newGenes = new double[genes.length];
    
    for (int i = 0; i < genes.length; i++) {
      if (generator.nextDouble() < rate) {
        newGenes[i] = genes[i] + generator.nextDouble() * range - range / 2;
      } else {
        newGenes[i] = genes[i];
      }
    }
    newNetwork.setWeights(newGenes);
  }

  public int compareTo(EvolutionSnake other) {
    return (int) (other.score - this.score);
  }
  
  public String toString() {
    return "EvoSnake" + id + "{ Score: " + score + " Death: " + death + " }";
  }
}
