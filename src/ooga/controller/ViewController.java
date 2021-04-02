package ooga.controller;

import java.util.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ooga.model.grid.BoardState;
import ooga.model.player.Elements;
import ooga.view.CommandBlock;
import ooga.view.ScreenCreator;

/**
 * @author Ji Yun Hyo
 */
public class ViewController implements FrontEndExternalAPI {

    BackEndExternalAPI modelController;
    ScreenCreator screenCreator;
    /**
     * Default constructor
     * @param stage
     */
    public ViewController(Stage stage) {
        screenCreator = new ScreenCreator(this, stage);
    }
    /**
     *
     * sets the model controller to set up the line of communication from/to the backend
     *
     * @param modelController BackEndExternalAPI
     */
    @Override
    public void setModelController(BackEndExternalAPI modelController) {
        this.modelController = modelController;
    }

    /**
     * Sets the view board to contain a new level. Instantiates all the elements of the grid,
     * including the dimensions and initial locations of humans and objects.
     *
     * @param boardState The initial state of the board
     */
    @Override
    public void setBoard(BoardState boardState) {

    }

    /**
     * Updates and individual sprite (avatars, block)
     *
     * @param id         Id of the sprite to be updated
     * @param spriteData Representation of element of the game
     */
    @Override
    public void updateSprite(int id, Elements spriteData) {

    }

    /**
     * Sets the position of the sprite
     *
     * @param x
     * @param y
     * @param id
     */
    @Override
    public void setPosition(double x, double y, int id) {

    }

    /**
     * Passes in the commands to be parsed and executed
     *
     * @param commandBlocks List of individual command blocks derived from the blocks in the
     *                      CodeBuilderArea
     */
    @Override
    public void parseAndExecuteCommands(List<CommandBlock> commandBlocks) {
        modelController.parseAndExecuteCommands(commandBlocks);
    }
}