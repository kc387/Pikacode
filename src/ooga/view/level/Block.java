package ooga.view.level;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Block extends StackPane {

  private static final double PADDING_RATIO = 0.25;
  private static final double PICKEDUP_SHIFT = 0.5;

  private int initialXCoordinate;
  private int initialYCoordinate;
  private double width;
  private double height;
  private double padding;
  private Text blockText;
  private SpriteLayer spriteLayer;
  private String number;
  private double currentX;
  private double currentY;
  private Rectangle block;

  public Block(int x, int y, double w, double h, SpriteLayer root, String num) {
    initialXCoordinate = x;
    initialYCoordinate = y;
    width = w;
    height = h;
    padding = PADDING_RATIO * width;
    spriteLayer = root;
    number = num;
    currentX = initialXCoordinate * width + padding;
    currentY = initialYCoordinate * height + padding;
    makeBlock();
  }

  public void moveBlock(double x, double y) {
    this.setTranslateX(x * width + padding);
    currentX = block.getX();
    this.setTranslateY(y * height + padding);
    currentY = block.getY();
    System.out.println(currentX);
    System.out.println(currentY);
  }

  public void animatePickUp(double percent) {
    System.out.println(currentY);
    this.setTranslateY(currentY - width * percent);
    currentY = this.getTranslateY();
    System.out.println(currentY);
  }

  public void animateDrop(double percent) {
    System.out.println(currentY);
    this.setTranslateY(currentY + width * percent);
    currentY = this.getTranslateY();
    System.out.println(currentY);
  }

  /**
   * Purpose: update number on datacube based on what backend/controller passes.
   * @param num
   */
  public void updateCubeNumber(int num) {
    this.getChildren().remove(blockText);
    number = String.valueOf(num);
    blockText.setText(number);
    this.getChildren().add(blockText);
  }

  public void reset() {
    this.setTranslateX(initialXCoordinate * width + padding);
    this.setTranslateY(initialYCoordinate * height + padding);
  }

  public int getInitialXCoordinate(){
    return (int) (block.getX()/width);
  }

  public int getInitialYCoordinate(){
    return (int) (block.getY()/height);
  }

  private void makeBlock() {
    block = new Rectangle(width - 2 * padding, height - 2 * padding);
    block.setFill(Color.LIGHTSEAGREEN); // TODO: put in resource file

    blockText = new Text(number);
    this.getChildren().add(block);
    this.getChildren().add(blockText);
    reset();
    spriteLayer.getChildren().add(this);
  }
}
