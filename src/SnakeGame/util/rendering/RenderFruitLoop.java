package SnakeGame.util.rendering;
import java.awt.Color;
import SnakeGame.util.*;
import objectdraw.*;

public class RenderFruitLoop extends FruitLoop{

  public FilledOval innerCircle;
  public FilledOval outerCircle;
  
  private static final int HALF_DIVISOR = 2;
  
  public RenderFruitLoop(GridCell cell, Color color, DrawingCanvas canvas) {
    
    super(cell);
    
    int centerX = cell.getCol() * PA8Constants.GRID_CELL_SIZE 
        + PA8Constants.GRID_CELL_SIZE / HALF_DIVISOR;
    int centerY = cell.getRow() * PA8Constants.GRID_CELL_SIZE 
        + PA8Constants.GRID_CELL_SIZE / HALF_DIVISOR;
    
    double innerRadius = PA8Constants.GRID_CELL_SIZE 
        / PA8Constants.INNER_SIZE_DIVISOR;
    double outerRadius = PA8Constants.GRID_CELL_SIZE 
        / PA8Constants.OUTER_SIZE_DIVISOR;
    
    outerCircle = new FilledOval(centerX - outerRadius / HALF_DIVISOR,
        centerY - outerRadius / HALF_DIVISOR, 
        outerRadius, outerRadius, canvas);
    innerCircle = new FilledOval(centerX - innerRadius / HALF_DIVISOR,
        centerY - innerRadius / HALF_DIVISOR, 
        innerRadius, innerRadius, canvas);
    
    innerCircle.setColor(PA8Constants.BACKGROUND_COLOR);
    outerCircle.setColor(color);
  }
  
  public void eaten() {
    super.eaten();
    innerCircle.removeFromCanvas();
    outerCircle.removeFromCanvas();
  }
}