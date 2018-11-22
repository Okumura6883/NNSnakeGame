package SnakeGame.trainer;
import java.util.ArrayList;
import org.neuroph.nnet.*;
import SnakeGame.SnakeGame;

public abstract class SnakeTrainer {
  
  public static final String NNPATH = "./Generations/";
  protected MultiLayerPerceptron nnet;
  public String name = "Snake Trainer";
  public SnakeGame game;
  public int dataRows = 0;
  public int limit;
  public int times;
  protected int[] neurons;
  public boolean done = false;
  
  protected SnakeTrainer(int times, int limit, 
      int inputNeurons, int[] neurons, int outputNeurons) {
    
    nnet = new MultiLayerPerceptron(new ArrayList<Integer>() {{
      add(inputNeurons);
      addAll(new ArrayList<Integer>() {{
        for(int neuron: neurons) {
          add(neuron);
        }
      }});
      add(outputNeurons);      
    }});
    
    this.neurons = neurons;
    this.times = times;
    this.limit = limit;
    setup();
  }
  
  public abstract void setup();
  
  public abstract void decide();
  
  public abstract void validateMove();
  
  public abstract void recordDeath();
  
  public abstract void recordFruit();

  public abstract void done();
}
