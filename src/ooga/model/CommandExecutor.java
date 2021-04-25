package ooga.model;

import com.google.common.base.Stopwatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import ooga.controller.BackEndExternalAPI;
import ooga.model.commands.Commands;
import ooga.model.grid.ElementInformationBundle;
import ooga.model.grid.gridData.GoalState;
import ooga.model.grid.gridData.InitialState;
import ooga.model.player.Player;
import ooga.view.level.codearea.CommandBlock;

/**
 * @author Ji Yun Hyo
 */
public class CommandExecutor implements  Executor{

    private List<Commands> commandBlocks;
    private Map<Integer, CommandBlock> mapOfCommandBlocks;
    private BackEndExternalAPI modelController;
    private ElementInformationBundle elementInformationBundle;
    private int score;
    private int idealTime;
    private int idealLines;
    private ClassLoader classLoader;
    private final String COMMAND_CLASSES_PACKAGE = Commands.class.getPackageName();
    private GoalState goalState;
    private List<Integer> endCommandLines;
    private Map<Integer, Integer> idToCommandLines;
    private Stack<Integer> stackOfIfCommands;
    private Stopwatch stopwatch;
    private int timeLeft;
    private int timeLimit;
    /**
     * Default constructor
     */
    public CommandExecutor(List<CommandBlock> commandBlocks, BackEndExternalAPI modelController,
        InitialState initialState,
        ElementInformationBundle elementInformationBundle,
        GoalState goalState, Stopwatch stopwatch) {
        initializeVariables(modelController, initialState, elementInformationBundle, goalState, stopwatch);
        initializeDataStructures();
        buildCommandMap(commandBlocks);
        this.elementInformationBundle.setEndCommandLines(endCommandLines);
        this.elementInformationBundle.setMapOfCommandLines(idToCommandLines);
    }

    private void initializeVariables(BackEndExternalAPI modelController, InitialState initialState,
        ElementInformationBundle elementInformationBundle, GoalState goalState,
        Stopwatch stopwatch) {
        this.goalState = goalState;
        this.elementInformationBundle = elementInformationBundle;
        this.elementInformationBundle.setModelController(modelController);
        this.modelController = modelController;
        this.stopwatch = stopwatch;
        this.idealTime = goalState.getIdealTime();
        this.idealLines = goalState.getIdealLines();
        this.timeLimit = initialState.getTimeLimit();
        this.score = 0;
    }

    private void initializeDataStructures() {
        this.commandBlocks = new ArrayList<>();
        this.idToCommandLines = new TreeMap<>();
        this.endCommandLines = new ArrayList<>();
        this.stackOfIfCommands = new Stack<>();
        this.classLoader = new ClassLoader() {
        };
        this.mapOfCommandBlocks = new HashMap<>();
    }

    private void buildCommandMap(List<CommandBlock> commandBlocks) {
        for(CommandBlock commandBlock : commandBlocks){
            mapOfCommandBlocks.put(commandBlock.getIndex(), commandBlock);
            Commands newCommand = null;
            try {
                Class r = classLoader.loadClass(COMMAND_CLASSES_PACKAGE+"."+ commandBlock.getType().replaceAll("\\s", "").substring(0,1).toUpperCase() + commandBlock.getType().replaceAll("\\s", "").substring(1));
                Object command = r.getDeclaredConstructor(ElementInformationBundle.class, Map.class).newInstance(elementInformationBundle, commandBlock.getParameters());
                newCommand = (Commands) command;
            }catch (Exception ignored){
                System.out.println("Failed");
            }
            this.commandBlocks.add(newCommand);
            findEndCommands(commandBlock);
        }
    }

    private void findEndCommands(CommandBlock commandBlock) {
        if(commandBlock.getType().equals("if")){
            stackOfIfCommands.add(commandBlock.getIndex());
        }
        if(commandBlock.getType().equals("end if")){
            idToCommandLines.put(stackOfIfCommands.pop(), commandBlock.getIndex());
        }
    }

    public void runNextCommand() {
        boolean allCommandsFinishedExecuting = true;
        Map<Integer, Integer> lineUpdates = new HashMap<>();
        for(Player avatar : elementInformationBundle.getAvatarList()){
            allCommandsFinishedExecuting = executeCommandsOnAvatar(allCommandsFinishedExecuting, lineUpdates, avatar);
        }
        modelController.setLineIndicators(lineUpdates);
        if(allCommandsFinishedExecuting){
            modelController.declareEndOfRun();
            score = 0;
        }
        checkTimeLeftOrNot();

    }

    private boolean executeCommandsOnAvatar(boolean ended, Map<Integer, Integer> lineUpdates, Player avatar) {
        if (avatar.getProgramCounter() < commandBlocks.size() + 1){
            ended = false;
            lineUpdates.put(avatar.getId(), avatar.getProgramCounter());
            commandBlocks.get(avatar.getProgramCounter() - 1).execute(avatar.getId());
            score++;
            modelController.setScore(goalState.getNumOfCommands() - score);
        }
        if(goalState.checkGameEnded(elementInformationBundle)){
            ended = true;
            List<Integer> scores = calculateFinalScores(idealLines, idealTime);
            modelController.winLevel(goalState.getNumOfCommands() - score, scores.get(0), scores.get(1));
        }
        if((goalState.getNumOfCommands() - score) < 0){
            modelController.setScore(0);
            modelController.loseLevel();
            score = 0;
        }

        return ended;
    }

    private List<Integer> calculateFinalScores(int idealLines, int idealTime) {
        List<Integer> scores = new ArrayList<>();
        timeLeft = (int) (timeLimit - stopwatch.elapsed(TimeUnit.SECONDS));
        int SCORING_FACTOR = 10;
        scores.add((idealLines - commandBlocks.size()) * SCORING_FACTOR);
        scores.add((timeLeft/60) * SCORING_FACTOR);
        return scores;
    }

    public void checkTimeLeftOrNot() {
        timeLeft = (int) (timeLimit - stopwatch.elapsed(TimeUnit.SECONDS));
        if(timeLeft <= 0){
            modelController.updateTime(0);
            modelController.timedOut();
        }else{
            modelController.updateTime(timeLeft);
        }
    }
}