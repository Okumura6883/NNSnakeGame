package SnakeGame.util;
import java.util.Random;
import SnakeGame.SnakeGame;
import objectdraw.*;

public class Grid {

  private GridCell[][] cells;
  
  private int rows;
  private int cols;
  private int empty;
  public Random generator;
  
  public Grid(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    generator = new Random();
    reset();
  }

  public void reset() {
    cells = new GridCell[rows][cols];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        cells[i][j] = new GridCell(i, j, this);
      }
    }
    empty = rows * cols;
  }
  
  public int getHeight() {
    return rows * PA8Constants.GRID_CELL_SIZE;
  }
  
  public int getWidth() {
    return cols * PA8Constants.GRID_CELL_SIZE;
  }
  
  public GridCell getCell(int row, int col) {
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
      return null;
    }
    return cells[row][col];
  }
  
  public GridCell getRandomEmptyCell() {
    if (empty == 0) {
      return null;
    }
    GridCell cell;
    do {
      cell = getCell(generator.nextInt(rows), generator.nextInt(cols));
    } while (!cell.isEmpty());
    return cell;
  }
  
  public GridCell getCellNeighbor(GridCell cell, Direction dir) {
    if (cell == null) {
      return null;
    }
    return getCell(cell.getRow() + dir.getY(), cell.getCol() + dir.getX());
  }
  
  public int deadBlock(GridCell cell) {
    if (cell == null) {
      return 1;
    } else {
      if (cell.hasSnake()) {
        return 1;
      } else {
        return 0;
      }
    }
  }
  
  public void incrementEmpty() {
    empty++;
  }
  
  public void decrementEmpty() {
    empty--;
  }
  
  public void draw(DrawingCanvas canvas) {
    FilledRect background  = new FilledRect(0, 0, 
        getWidth(), getHeight(), canvas);
    background.setColor(PA8Constants.BACKGROUND_COLOR);
    for (int i = 0; i <= rows; i++) {
      Line line = new Line(0, i * PA8Constants.GRID_CELL_SIZE, 
          getWidth(), i * PA8Constants.GRID_CELL_SIZE, canvas);
      line.setColor(PA8Constants.GRID_LINE_COLOR);
    }
    
    for (int i = 0; i <= cols; i++) {
      Line line = new Line(i * PA8Constants.GRID_CELL_SIZE, 0, 
          i * PA8Constants.GRID_CELL_SIZE, getHeight(), canvas );
      line.setColor(PA8Constants.GRID_LINE_COLOR);
    }
  }
  
  public String toString() {
    String s = "";
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        if (cells[i][j].isEmpty()) {
          s += "o";
        } else if (cells[i][j].hasFruit()) {
          s += "F";
        } else if (cells[i][j].hasSnake()) {
          s += "S";
        }
      }
      s += "\n";
    }
    return s;
  }
}
