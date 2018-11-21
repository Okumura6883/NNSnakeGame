package SnakeGame.util;

public class FruitLoop {

  private GridCell cell;
  
  public FruitLoop(GridCell cell) {
    this.cell = cell;
    cell.addFruit(this);
  }
  
  public void eaten() {
    cell.removeFruit();
  }
  
  public int getX() {
    return cell.getCol();
  }
  
  public int getY() {
    return cell.getRow();
  }
  
  public String toString() {
    return "Fruit [ " + getX() + ", " + getY() + "]";
  }
}