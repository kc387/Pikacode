package ooga.model.commands;

import java.util.Map;
import ooga.model.grid.ElementInformationBundle;

/**
 * Subtract is a type of Mathematical Command that takes the difference of the display number of two
 * block, one held by the Avatar by the other on the tile that the Avatar is standing on, and sets
 * this value to the block held by the Avatar.
 *
 * @author Harrison Huang
 */
public class Subtract extends MathematicalCommands {

  /**
   * Base constructor of a command. Takes in an ElementInformationBundle and parameters custom to
   * the type of command.
   *
   * @param elementInformationBundle The ElementInformationBundle of the game
   * @param parameters               A Map of parameters to the command
   */
  public Subtract(ElementInformationBundle elementInformationBundle,
      Map<String, String> parameters) {
    super(elementInformationBundle, parameters);
  }

  /**
   * The formula for calculating the new display number of the block held by the avatar. The
   * implementation and formula of the new display number is to be handled by subclasses.
   *
   * @param avatarCubeNum The current number of the block held by the avatar
   * @param tileCubeNum   The current number of the block on the tile
   * @return The new number to be set to the avatar block
   */
  @Override
  public int calculateNewDisplayNum(int avatarCubeNum, int tileCubeNum) {
    return avatarCubeNum - tileCubeNum;
  }
}