package SnakeGame;

import org.neuroph.core.NeuralNetwork;
import Acme.MainFrame;
import SnakeGame.trainer.EvolutionSnake;
import SnakeGame.trainer.SnakeTrainer;
import objectdraw.WindowController;

public class AI_Tester extends WindowController{

  private static SnakeGame game;
  
  public AI_Tester() {
    // TODO Auto-generated constructor stub
  }
  
  public void begin() {
    game.play();
  }

  public static void main(String[] args) {
    AI_Tester window = new AI_Tester();
    EvolutionSnake snake = EvolutionSnake.createSnake();
    snake.setNnet(NeuralNetwork.createFromFile(SnakeTrainer.NNPATH + "Gen250/Parent1.nnet"));
    game = new SnakeGame(snake, window.canvas);
    new MainFrame(window, args, game.grid.getWidth(), game.grid.getHeight());
  }

}
