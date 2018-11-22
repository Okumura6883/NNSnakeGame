package SnakeGame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import Acme.*;
import SnakeGame.trainer.*;
import SnakeGame.util.*;
import SnakeGame.util.rendering.RenderFruitLoop;
import objectdraw.*;


public class SnakeGame implements Runnable{

  private static final int DELAY = 20;
  
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
  
  public SnakeTrainer trainer;
  private int trainTimes;
  private int gameCount = 0;
  private DrawingCanvas canvas;
  public boolean render;
  
  public SnakeGame(SnakeTrainer trainer) {
    this.grid = new Grid(PA8Constants.DEFAULT_ROWS, PA8Constants.DEFAULT_COLS);
    this.delay = DELAY;
    this.render = false;
    this.trainer = trainer;
    this.trainer.game = this;
    this.trainTimes = trainer.times;
  }
  
  public SnakeGame(SnakeTrainer trainer, DrawingCanvas canvas) {
    this(trainer);
    this.canvas = canvas;
    this.render = true;
  }
  
  public void play() {
    for (int i = 0; i <= trainTimes; i++) {
      initGame();
    }
  }
  
  public void initGame() {

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
    getSnake().run();
  }
  
  public DrawingCanvas getCanvas() {
    return canvas;
  }
  
  public AI_Snake getSnake() {
    return snake;
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
    mostStep = Math.max(mostStep, getSnake().moves);
    fruit = null;
    fruitEaten = 0;
    gameCount++;
    totalStep += getSnake().moves;
  }
  
  private Color getNewFruitColor() {
    return PA8Constants.FRUIT_LOOP_COLORS[fruitIndex++ 
                                 % PA8Constants.FRUIT_LOOP_COLORS.length];
  }
  
  @Override
  public void run() {
    // System.out.println(Thread.currentThread().getName() + " Starting");
    play();
    trainer.done();
  }
}
