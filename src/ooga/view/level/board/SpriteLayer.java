package ooga.view.level.board;

import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.Pane;
import ooga.model.grid.gridData.BlockData;
import ooga.view.ScreenCreator;
import ooga.view.animation.Animation;

/**
 * SpriteLayer class is responsible for displaying the avatars and the blocks. This layer is
 * separate from the board grid because we wanted the elements to be displayed separately from the grid
 * to allow for maximally customizable.
 * @author Ji Yun Hyo
 */
public class SpriteLayer extends Pane {

  private double xSize;
  private double ySize;

  private Map<Integer, ViewAvatar> avatars;
  private Map<Integer, ViewBlock> blocks;

  private Map<String, List<Integer>> initialAvatarLocations;
  private Map<String, BlockData> initialBlockData;
  private final Animation animation;
  private Map<Integer, Deque<Double>> allElementInformation;

  /**
   * Sets the layer that is going to be used to display all the elements of the game. This was to
   * make sure the separation between the tiles and the elements.
   * @param width width of the sprite layer
   * @param height height of the sprite layer
   */
  public SpriteLayer(double width, double height) {
    this.setId(ScreenCreator.idsForTests.getString("spriteLayer"));
    this.setMinSize(width, height);
    this.setMaxSize(width, height);
    animation = new Animation();
  }

  /**
   * Initialize all the avatar locations on the grid according to the parsed information received
   * @param allAvatarLocations display an avatar at each of  the locations
   */
  public void initializeAvatars(Map<String, List<Integer>> allAvatarLocations) {
    avatars = new HashMap<>();
    initialAvatarLocations = allAvatarLocations;
    initialAvatarLocations.forEach((id, location) -> {
      ViewAvatar viewAvatar = new ViewAvatar(location.get(0), location.get(1), xSize, ySize,
          Integer.parseInt(id), this);
      avatars.put(Integer.parseInt(id), viewAvatar);
    });
  }

  /**
   * Initializes all the blocks according to the template
   * @param allBlockData data of blocks read in from the template
   */
  public void initializeBlocks(Map<String, BlockData> allBlockData) {
    blocks = new HashMap<>();
    initialBlockData = allBlockData;
    initialBlockData.forEach((id, blockData) -> {
      ViewBlock viewBlock = new ViewBlock(blockData.getLocation().get(0),
          blockData.getLocation().get(1),
          xSize,
          ySize, this, "" + blockData.getBlockNumber());
      blocks.put(Integer.parseInt(id), viewBlock);
    });
  }

  /**
   * Sets the size of the grip
   * @param xSize size in x
   * @param ySize size in y
   */
  public void setSizes(double xSize, double ySize) {
    this.xSize = xSize;
    this.ySize = ySize;
  }

  /**
   * Updates the avatar position
   * @param id ID of the avatar being updated
   * @param xCoord new x coordinate
   * @param yCoord new y coordinate
   */
  public void updateAvatarPosition(int id, int xCoord, int yCoord) {
    ViewAvatar viewAvatar = avatars.get(id);
    animation.queuePositionUpdates(id, viewAvatar.getInitialXCoordinate(),
        viewAvatar.getInitialYCoordinate(), xCoord, yCoord);
  }

  /**
   * Updates the block position
   * @param id ID of the block
   * @param xCoord new x coordinate
   * @param yCoord new y coordinate
   */
  public void updateBlockPosition(int id, int xCoord, int yCoord) {
    ViewBlock viewBlock = blocks.get(id);
    animation.queuePositionUpdates(id, viewBlock.getInitialXCoordinate(),
        viewBlock.getInitialYCoordinate(), xCoord, yCoord);
  }

  /**
   * Update the animation of the block for picked up and not picked up states
   * @param id ID of the block
   * @param isHeld boolean indicating whether the block is held
   */
  public void updateBlock(int id, boolean isHeld) {
    blocks.get(id).setHeldStatus(isHeld);
    if (isHeld) {
      blocks.get(id).setShiftHeight(1);
    } else {
      blocks.get(id).setShiftHeight(0);
    }
  }

  /**
   * Set the number on the block.
   * @param id ID of the block
   * @param newDisplayNum the number to be displayed
   */
  public void setBlockNumber(int id, int newDisplayNum) {
    blocks.get(id).updateCubeNumber(newDisplayNum);
  }

  /**
   * Update the animation and tell the view to display the updated animation
   * @return boolean to indicate whether the updating animation has finished
   */
  public boolean updateAnimationForFrontEnd() {
    allElementInformation = animation.getAllElementInformation();
    boolean finished = true;
    for (Map.Entry<Integer, Deque<Double>> entry : allElementInformation.entrySet()) {
      if (!entry.getValue().isEmpty()) {
        double nextX = entry.getValue().pop();
        double nextY = entry.getValue().pop();
        if (avatars.containsKey(entry.getKey())) {
          avatars.get(entry.getKey()).moveAvatar(nextX, nextY);
        } else if (blocks.containsKey(entry.getKey())) {
          blocks.get(entry.getKey()).moveBlock(nextX, nextY);
        }
        finished = false;
      }
    }
    return finished;
  }

  /**
   * Resets the ques in animation to start over
   */
  public void resetAnimationQueue() {
    animation.reset();
    allElementInformation = new HashMap<>();
  }

  /**
   * Resets the animation of avatars when the grid starts over
   */
  public void resetAvatarLocations() {
    initialAvatarLocations.forEach((id, location) -> avatars.get(Integer.parseInt(id)).reset());
  }

  /**
   * Resets the animation of blocks when the grid starts over
   */
  public void resetBlockData() {
    initialBlockData.forEach((id, blockData) -> blocks.get(Integer.parseInt(id)).reset());
  }

  /**
   * Resets the animation of avatar images when the grid starts over
   */
  public void resetAvatarImages() {
    if (allElementInformation != null) {
      for (Map.Entry<Integer, ViewAvatar> entry : avatars.entrySet()) {
        avatars.get(entry.getKey()).setAvatarImage("images/PikachuAvatar.gif");
      }
    }
  }

}
