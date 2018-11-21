package SnakeGame.util;
import objectdraw.*;

public class GridCell {
  
  private Location pos;
  private int row;
  private int col;
  
  private FruitLoop fruit;
  private SnakeSegment snake;
  private Grid grid;

  public GridCell(int row, int col, Grid grid) {
    this.row = row;
    this.col = col;
    this.grid = grid;
    pos = new Location(row * PA8Constants.GRID_CELL_SIZE, 
        col * PA8Constants.GRID_CELL_SIZE);
    
  }
  
  public boolean isEmpty() {
    return fruit == null && snake == null;
  }
  
  public boolean hasSnake() {
    return snake != null;
  }
  
  public boolean hasFruit() {
    return fruit != null;
  }
  
  public void removeFruit() {
    grid.decrementEmpty();
    fruit = null;
  }
  
  public void removeSnake() {
    grid.decrementEmpty();
    snake = null;
  }
  
  public void addSnake(SnakeSegment snake) {
    grid.incrementEmpty();
    this.snake = snake;
  }
  
  public void addFruit(FruitLoop fruit) {
    grid.incrementEmpty();
    this.fruit = fruit;
  }

  public int getRow() {
    return row;
  }

  public int getCol() {
    return col;
  }

  public Location getPos() {
    return pos;
  }

}
