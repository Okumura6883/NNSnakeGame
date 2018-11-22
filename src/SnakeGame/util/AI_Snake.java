package SnakeGame.util;

import java.util.ArrayList;
import SnakeGame.SnakeGame;
import SnakeGame.trainer.SnakeTrainer;
import SnakeGame.util.rendering.*;

public class AI_Snake {

  public Direction dir = Direction.UP;
  public SnakeGame game;
  protected SnakeSegment head;
  protected ArrayList<SnakeSegment> body;
  protected Grid grid;
  protected SnakeTrainer trainer;
  public int moves = 0;
  public boolean alive = true;
  public int starve = 0;

  public AI_Snake(SnakeGame game, Grid grid, SnakeTrainer trainer) {
    this.game = game;
    this.grid = grid;
    this.trainer = trainer;
    body = new ArrayList<SnakeSegment>();
    GridCell startCell = grid.getRandomEmptyCell();
    if (game.render) {
      head = new RenderSnakeSegment(startCell, game.getCanvas());
    } else {
      head = new SnakeSegment(startCell);
    }
  }
  
  public void run() {
    
    while (alive) {
      
      if (game.render) {
        pause(game.delay);
      }
      
      if (game.gameState == PA8Constants.GAME_RUNNING) {

        trainer.decide();
        
        // System.out.println("Snake made a move: " + moves);
        
        moves++;
        
        if (!move()) {
          die();
        }
        
        trainer.validateMove();
      }
    }
  }
  
  private void pause(int time) {
    try {
      // System.out.println("Sleeping: " + time);
      Thread.sleep(time);
    } catch (InterruptedException e) {

      e.printStackTrace();
    }
  }
  
  public void toggle() {
    
    System.out.println("Toggling game mode?!!");
    
    if (game.gameState == PA8Constants.GAME_RUNNING) {
      game.gameState = PA8Constants.GAME_PAUSED;
      return;
    }
    if (game.gameState == PA8Constants.GAME_PAUSED) {
      game.gameState = PA8Constants.GAME_RUNNING;
      return;
    }
  }
  
  public void setDirection(Direction dir) {
    if (game.gameState == PA8Constants.GAME_RUNNING) {
      if (body.isEmpty() || !this.dir.isOpposite(dir)) {
        this.dir = dir;        
      }
    }
  }
  
  public SnakeSegment getHead() {
    return head;
  }
  
  private boolean move() {
    if (dir == Direction.NONE) {
      return true;
    } else {
      boolean grow = false;
      GridCell nextCell = grid.getCellNeighbor(head.getCell(), dir);
      
      if (nextCell == null || nextCell.hasSnake()) {
        return false;
      } else if (nextCell.hasFruit()){
        game.respawnFruit();
        trainer.recordFruit();
        grow = true;
        starve = 0;
      } else {
        starve++;
      }
      
      if (starve == game.trainer.limit) {
        die();
      }
      
      GridCell lastCell = head.getCell();
      head.moveTo(nextCell);
      
      for (int i = 0; i < body.size(); i++) {
        GridCell tempCell = body.get(i).getCell();
        body.get(i).moveTo(lastCell);
        lastCell = tempCell;
      }
      
      if (grow) {
        if (game.render) {
          body.add(new RenderSnakeSegment(lastCell, game.getCanvas()));
        } else {
          body.add(new SnakeSegment(lastCell));
        }
      }
      return true;
    }
  }
  
  public int hasObstacle(Direction dewae) {
    
    if (dewae == Direction.LEFT) {
      
      return grid.deadBlock(grid.getCellNeighbor(head.getCell(), dir.toLeft()));
      
    } else if (dewae == Direction.RIGHT) {
      
      return grid.deadBlock(grid.getCellNeighbor(head.getCell(), dir.toRight()));
      
    } else if (dewae == Direction.UP) {
      
      return grid.deadBlock(grid.getCellNeighbor(head.getCell(), dir));
      
    } else if (!started() && dewae == Direction.DOWN){

      return grid.deadBlock(grid.getCellNeighbor(head.getCell(), dir.toOpposite()));
      
    } else {
      return 0;
      
    }
    
  }
  
  private void die() {
    // System.out.print("Snake died at move " + moves);
    alive = false;
    trainer.recordDeath();
    game.gameover(false);
  }
  
  public int getSnakeLength() {
    return body.size() + 1;
  }
  
  public boolean started() {
    return dir != Direction.NONE;
  }
}
