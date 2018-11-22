package SnakeGame.trainer;

import java.util.Random;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.random.WeightsRandomizer;
import SnakeGame.util.*;

public class EvolutionSnake extends SnakeTrainer implements Comparable<EvolutionSnake>{

  public static final int DEFAULT_LIMIT = 100;
  public static final int DEFAULT_TIMES = 100;
  public static final int DEFAULT_INPUT = 5;
  public static final int DEFAULT_HIDDEN_NEURONS = 16;
  public static final int DEFAULT_OUTPUT = 1;
  private static Random generator = new Random();
  public double score;
  public int death;
  public int id;
  public int totalFruit;
  
  private EvolutionSnake(int times, int limit, int id, int... neurons) {
    super(times, limit, DEFAULT_INPUT, neurons, DEFAULT_OUTPUT);
    this.name = "Evo Snake";
    this.id = id;
  }
  
  public static EvolutionSnake createSnake() {
    return createSnake(0, new int[] { DEFAULT_HIDDEN_NEURONS, DEFAULT_HIDDEN_NEURONS});
  }
  
  public static EvolutionSnake createSnake(int id) {
    return createSnake(id, Population.DEFAULT_NEURONS, 
        new int[] { DEFAULT_HIDDEN_NEURONS, DEFAULT_HIDDEN_NEURONS});
  }

  public static EvolutionSnake createSnake(int id, int... neurons) {
    return createSnake(DEFAULT_TIMES, id, neurons);
  }
  
  public static EvolutionSnake createSnake(int times, int id, int... neurons) {
    return new EvolutionSnake(times, DEFAULT_LIMIT, id, neurons);
  }
  
  public void decide() {
    
    int leftBlocked = game.getSnake().hasObstacle(Direction.LEFT);
    int frontBlocked = game.getSnake().hasObstacle(Direction.UP);
    int rightBlocked = game.getSnake().hasObstacle(Direction.RIGHT);
    
    int choice = 0;
    double reward = -1;
    
    for (int i = -1; i <= 1; i++) {
      nnet.setInput(leftBlocked, frontBlocked, rightBlocked, angleToFruit(), i);
      nnet.calculate();
      double result = nnet.getOutput()[0];

      if (result > reward) {
        reward = result;
        choice = i;
      }
    }
    // System.out.println("Computer choose: " + choice);
    if (choice == -1) {
      game.getSnake().setDirection(game.getSnake().dir.toLeft());
    }
    if (choice == 1) {
      game.getSnake().setDirection(game.getSnake().dir.toRight());
    }
  }
  
  public void recordDeath() {
//    death++;
//    score -= 100 / (times * limit);
  }
  
  public void recordFruit() {
    score += 1000 * Math.pow(1.2, game.getSnake().game.fruitEaten) / times;
  }
  
  public void validateMove() {
    score -= 100 / (times * limit);
  }
  
  public EvolutionSnake copyFromGenes(double[] genes) {
    EvolutionSnake copy = new EvolutionSnake(times, limit, id, neurons);
    copy.nnet.setWeights(genes);
    return copy;
  }
  
  public void setNnet(NeuralNetwork n) {
    nnet = (MultiLayerPerceptron) n;
  }
  
  public EvolutionSnake crossover(EvolutionSnake mate) {
    Double[] genes = nnet.getWeights();
    Double[] mateGenes = mate.nnet.getWeights();
    
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
  
  @Override
  public void setup() {
    nnet.randomizeWeights(new WeightsRandomizer(new Random()));
    BackPropagation lr = new BackPropagation();
    lr.setMaxIterations(10);
    lr.setLearningRate(0.1);
    lr.setMaxError(0.025);
    nnet.setLearningRule(lr);
  }
  
  public void reset() {
    score = 0;
    totalFruit = 0;
    done = false;
  }

  public void save(String path) {
    nnet.save(path);
  }
  
  private double angleToFruit() {
    
    FruitLoop fruit = game.fruit;
    
    int dx = game.getSnake().getHead().getX() - fruit.getX();
    int dy = game.getSnake().getHead().getY() - fruit.getY();

    double angle = 0;
    
    if (dx == 0) {
      angle = -Math.signum(dy) * Math.PI / 2;
    } else {
      angle = -Math.atan2(dy, dx);
    }
    angle += Direction.DIRECTIONS.indexOf(game.getSnake().dir) * Math.PI / 2;
    if (angle > Math.PI) {
      angle -= 2 * Math.PI;
    }
    
    return angle / Math.PI;
  }
  
  public void mutate(double rate, double range) {
    Double[] genes = nnet.getWeights();
    double[] newGenes = new double[genes.length];
    
    for (int i = 0; i < genes.length; i++) {
      if (generator.nextDouble() < rate) {
        newGenes[i] = genes[i] + generator.nextDouble() * range - range / 2;
      } else {
        newGenes[i] = genes[i];
      }
    }
    nnet.setWeights(newGenes);
  }

  @Override
  public int compareTo(EvolutionSnake other) {
    return (int) (other.score - this.score);
  }
  
  @Override
  public String toString() {
    return "EvoSnake " + id + "{Score: " + score + "}";
  }

  @Override
  public void done() {
    totalFruit += game.totalFruit;
    done = true;
    System.out.print(".");
  }
}
