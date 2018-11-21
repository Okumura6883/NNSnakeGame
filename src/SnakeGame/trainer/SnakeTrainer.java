package SnakeGame.trainer;
import org.neuroph.core.*;
import org.neuroph.core.data.*;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.learning.BackPropagation;
import SnakeGame.SnakeGame;
import SnakeGame.util.AI_Snake;

public abstract class SnakeTrainer implements LearningEventListener{
  
  public static final String NNPATH = "./Generations/";
  public String name = "Snake Trainer";
  public static final int LEARN = 0;
  public static final int TEST = 1;
  protected NeuralNetwork newNetwork;
  protected NeuralNetwork baseNetwork;
  public AI_Snake snake;
  protected DataSet data;
  protected String path;
  protected String base;
  public SnakeGame game;
  public int dataRows = 0;
  public int limit;
  public int mode;
  public int times;
  public int neurons;
  public boolean done = false;
  
  protected SnakeTrainer(String path, String base, int mode, int times, int limit, int neurons) {
    
    if (mode != LEARN && mode != TEST) {
      System.out.println("Unkown Mode");
      System.exit(1);
    }

    // System.out.println("Loading Snake Trainer");
    
    this.neurons = neurons;
    this.times = times;
    this.limit = limit;
    this.mode = mode;
    if (base == null) {
      this.base = null;
    } else {
      this.base = NNPATH + base + ".nnet";
    }
    this.path = NNPATH + path + ".nnet";
    setupBaseNetwork();
    setupNewNetwork();
  }
  
  public abstract void runTest();
  
  public abstract void runLearn();
  
  public abstract void setupNewNetwork();
  
  public void setupBaseNetwork() {
    if (base != null) {
      System.out.println("Reading network file: " + base);
      baseNetwork = NeuralNetwork.createFromFile(base);
    }
  }
  
  public void decide() {
    if (mode == TEST) {
      runTest();
    } else if (mode == LEARN) {
      runLearn();
    }
  }
  
  protected void record(DataSetRow... rows) {
    if (mode == TEST) {
      return;
    }
    for (DataSetRow row : rows) {
      if (row == null) {
        continue;
      }
      data.addRow(row);
      dataRows++;
    }
  }
  
  protected void record(int weight, DataSetRow... rows) {
    for (int i = 0; i < weight; i++) {
      record(rows);
    }
  }
  
  public abstract void validateMove();
  
  public void learn() {
    
    System.out.println("\n---------RESULT---------"
        + "\n  Trainer: " + name
        + "\n  Trained Times: " + times
        + "\n  Total Fruit Eaten: " + game.totalFruit 
        + "\n  Average Fruit Eaten: " + (double) game.totalFruit / times
        + "\n  Most Fruit Eaten: " + game.mostFruit
        + "\n  Total Moves: " + game.totalStep
        + "\n  Average Moves: " + (double) game.totalStep / times
        + "\n  Most Moves: " + game.mostStep);
    
    if (mode == TEST) {
      return;
    }
    
    System.out.println("\nLearning from data... (Total Data Rows: " + dataRows + ")");
    newNetwork.learn(data);
    System.out.println("\nSaving network to file: " + path);
    newNetwork.save(path);
  }
  
  public void recordDeath() {
    
  }
  
  public void recordFruit() {
    
  }
  
  public void handleLearningEvent(LearningEvent event) {
    BackPropagation lr = (BackPropagation)event.getSource();
    if(!lr.isStopped()) {
      System.out.println("\nCurrent Iteration: " + lr.getCurrentIteration() 
      + "\nNet Error: " + lr.getTotalNetworkError());
    }
  }

  public void done() {
    System.out.print(".");
  }
}
