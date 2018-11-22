package SnakeGame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import Acme.*;
import SnakeGame.trainer.*;
import SnakeGame.util.*;
import SnakeGame.util.rendering.RenderFruitLoop;
import objectdraw.*;


public class SnakeGame extends WindowController 
                       implements KeyListener, Runnable{
  
  public Grid grid;
  public int delay;
  
  public int mostFruit;
  public int fruitEaten;
  public int totalFruit;
  public int fruitIndex;
  public int totalStep;
  public int mostStep;
  
  public int gameState = PA8Constants.GAME_RUNNING;
  
  public FruitLoop fruit;
  private AI_Snake snake;
  
  private SnakeTrainer trainer;
  private int trainTimes;
  private int gameCount = 0;
  private static SnakeGame renderInstance;
  private static final int DELAY = 25;
  
  public SnakeGame(SnakeTrainer trainer, boolean render) {
    this.grid = new Grid(PA8Constants.DEFAULT_ROWS, PA8Constants.DEFAULT_COLS);
    this.delay = DELAY;
    this.render = render;
    this.trainer = trainer;
    this.trainer.game = this;
    this.trainTimes = trainer.times;
    if (render && renderInstance == null) {
      new MainFrame(this, grid.getWidth() + 1, 
          grid.getHeight() + 1);
      renderInstance = this;
    } else {
      render = false;
    }
  }
  
  public void begin() {
    canvas.addKeyListener(this);
    play();   
  }
  
  public boolean render = true;
  
  public void play() {
    
    for (int i = 0; i <= trainTimes; i++) {
      initGame();
    }
    trainer.learn();
  }
  
  public void initGame() {
    

    if (gameCount % 1000 == 0 || trainer.mode == SnakeTrainer.TEST && ! (trainer instanceof EvolutionSnake)) {
      
    }
    
    if (gameCount == trainTimes) {
      return;
    }
    
    if (render) {
      canvas.clear();
      grid.draw(canvas);
      // resetMessages();
    }
    
    gameState = PA8Constants.GAME_RUNNING;
    grid.reset();
    respawnFruit();
    snake = new AI_Snake(this, grid, trainer);
    snake.run();
  }
  
  public DrawingCanvas getCanvas() {
    return canvas;
  }
  
  public void respawnFruit() {

    if (fruit != null) {
      fruit.eaten();
      totalFruit++;
      fruitEaten++;
      if (render) {
      }
    } else {

    }
    if (render) {
      fruit = new RenderFruitLoop(grid.getRandomEmptyCell(), 
          getNewFruitColor(), canvas);
    } else {
      fruit = new FruitLoop(grid.getRandomEmptyCell());
    }
  }
  
  public void gameover(boolean win) {
    
    // System.out.print(" DataRows: " + trainer.dataRows);
    
    gameState = PA8Constants.GAME_OVER;
    mostFruit = Math.max(mostFruit, fruitEaten);
    mostStep = Math.max(mostStep, snake.moves);
    fruit = null;
    fruitEaten = 0;
    gameCount++;
    totalStep += snake.moves;
  }
  
  private Color getNewFruitColor() {
    return PA8Constants.FRUIT_LOOP_COLORS[fruitIndex++ 
                                 % PA8Constants.FRUIT_LOOP_COLORS.length];
  }

  public void keyPressed(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
    if (snake != null) {
      snake.toggle();
    }
  }

  public void keyTyped(KeyEvent e) {
    
  }

  public void run() {
    // System.out.println(Thread.currentThread().getName() + " Starting");
    play();
    trainer.done = true;
    trainer.done();
  }
}
