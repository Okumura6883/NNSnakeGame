package SnakeGame.util;
public class SnakeSegment {

  protected GridCell cell;
  
  public SnakeSegment(GridCell cell) {
    this.cell = cell;
    cell.addSnake(this);
  }
  
  public GridCell getCell() {
    return cell;
  }
  
  public int getX() {
    return cell.getCol();
  }
  
  public int getY() {
    return cell.getRow();
  }
  
  public void moveTo(GridCell cell) {
    this.cell.removeSnake();
    cell.addSnake(this);
    this.cell = cell;
  }
  
  public String toString() {
    return "Snake [ " + getX() + ", " + getY() + "]";
  }
}
