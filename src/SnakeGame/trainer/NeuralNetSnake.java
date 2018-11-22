package SnakeGame.trainer;

import java.util.Random;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.WeightsRandomizer;
import SnakeGame.SnakeGame;
import SnakeGame.util.*;

public class NeuralNetSnake extends SnakeTrainer{

  public static final String NAME = "Random2";
  private static Random generator = new Random();
  
  private NeuralNetSnake(int mode, int times, int limit, String base) {
    super(NAME, base, mode, times, limit, 0);
  }

  public static SnakeTrainer createNewTrainer(int mode) {
    return createNewTrainer(mode, 10000);
  }
  
  public static SnakeTrainer createNewTrainer(int mode, int times) {
    return createNewTrainer(mode, times, 1000);
  }
  
  public static SnakeTrainer createNewTrainer(int mode, int times, int limit) {
    if (mode == LEARN) {
      return new NeuralNetSnake(mode, times, limit, null);
    } else {
      return new NeuralNetSnake(mode, times, limit, NAME);
    }
  }
  
  private double[] input;
  private double lastDist;
  
  private Direction playRandomly() {
    return Direction.DIRECTIONS.get(generator.nextInt(3));
  }

  private void randomPlay() {
    
    int leftBlocked = snake.hasObstacle(Direction.LEFT);
    int frontBlocked = snake.hasObstacle(Direction.UP);
    int rightBlocked = snake.hasObstacle(Direction.RIGHT);
    double angle = angleToFruit();
    
    Direction dir = playRandomly();
    
    int turn = 0;
    
    if (dir == Direction.LEFT) {
      snake.setDirection(snake.dir.toLeft());
      turn = -1;
    } 
    if (dir == Direction.RIGHT) {
      snake.setDirection(snake.dir.toRight());
      turn = 1;
    }
    
    input = new double[] { leftBlocked, frontBlocked, rightBlocked, angle, turn};
    lastDist = distanceToFruit();
  }
  
  private void oldPlay() {
    
    int leftBlocked = snake.hasObstacle(Direction.LEFT);
    int frontBlocked = snake.hasObstacle(Direction.UP);
    int rightBlocked = snake.hasObstacle(Direction.RIGHT);
    
    int choice = 0;
    double reward = -1;
    
    for (int i = -1; i <= 1; i++) {
      baseNetwork.setInput(leftBlocked, frontBlocked, rightBlocked, angleToFruit(), i);
      baseNetwork.calculate();
      double result = baseNetwork.getOutput()[0];
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
  
  public void runTest() {
    oldPlay();
  }

  public void runLearn() {
    randomPlay();
  }

  public void setupNewNetwork() {
    
    newNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 5, 25, 25, 1);
    
    newNetwork.randomizeWeights(new WeightsRandomizer(new Random()));
    
    BackPropagation lr = new BackPropagation();
    
    lr.addListener(this);
    
    lr.setMaxIterations(10);
    lr.setLearningRate(0.1);
    lr.setMaxError(0.025);
    
    newNetwork.setLearningRule(lr);
    
    data = new DataSet(5, 1);
  }
  
  private int closer;
  private int far;
  
  public static void main(String[] args) {
    SnakeTrainer trainer = new NeuralNetSnake(TEST, 1000, 1000, "Gen85/Parent1");
    new SnakeGame(trainer, true);
  }

  public void validateMove() {
    if (mode == TEST) {
      return;
    }
    DataSetRow row;
    int weight;
    if (snake.alive) {
      if (lastDist >= distanceToFruit()) {
        row = new DataSetRow(input, new double[] {1});
        weight = 3;
        closer++;
      } else {
        row = new DataSetRow(input, new double[] {0});
        weight = 1;
        far++;
      }
      // row = new DataSetRow(input, new double[] {lastDist - distanceToFruit()});
    } else {
      row = new DataSetRow(input, new double[] {-1});
      weight = 10;
    }
    
    record(weight, row);
  }
  
  public void learn() {
    super.learn();
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
  
  private double distanceToFruit() {
    FruitLoop fruit = snake.game.fruit;
    double dx = snake.getHead().getX() - fruit.getX();
    double dy = snake.getHead().getY() - fruit.getY();
    return Math.sqrt(dx * dx + dy * dy);
  }
}
