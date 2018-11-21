package SnakeGame.util.rendering;
import java.awt.Color;
import SnakeGame.util.*;
import objectdraw.*;

public class RenderSnakeSegment extends SnakeSegment{
  
  private FilledOval body;
  
  public RenderSnakeSegment(GridCell cell, DrawingCanvas canvas) {
    super(cell);
    
    body = new FilledOval(0, 0, PA8Constants.GRID_CELL_SIZE, 
        PA8Constants.GRID_CELL_SIZE, canvas);
    body.setColor(Color.WHITE);
    draw();
  }
  
  private void draw() {
    body.moveTo(cell.getCol() * PA8Constants.GRID_CELL_SIZE,
        cell.getRow() * PA8Constants.GRID_CELL_SIZE);
  }
  
  public void moveTo(GridCell cell) {
    super.moveTo(cell);
    draw();
  }
}
