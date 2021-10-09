package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.BrainCopyMemoryAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.BrainDumpAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.BrainForgetAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.BrainPickAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.BrainRememberAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.CellChangeTypeAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.CellMakeRoadAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.CellPlanPathAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.CellSearchAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.EntityAddTagAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.EntityDrinkAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.EntityDumpTagAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.EntityEatAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.EntityExtractAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.EntityFollowPathAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.EntityRemoveTagAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.EntityRestAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.EntitySearchAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.FailureAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.LogAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.EntityNotifyDialogAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.SucceedAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.TimeGateAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.WanderAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.conditions.HasMemoryCondition;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.conditions.HasTagCondition;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.conditions.HungerCondition;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.conditions.MissingMemoryCondition;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.conditions.MissingTagCondition;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.conditions.ThirstCondition;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.conditions.EnergyCondition;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTCondition;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTItem;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTMultipleChildren;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTSingleChild;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.manage.BTLoop;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.manage.BTSequenceAll;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.manage.BTSequenceAny;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.manage.BTree;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.CommandParser;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.ParseException;

public class BTFactory {

    public static BTree getBehaviour(String filename) {
        String textData = Gdx.files.internal("behaviour/" + filename + ".btree").readString();
        BTree result;
        try {
            result = convertFromRaw(filename, textData);
        } catch (RuntimeException e) {
            throw new ParseException("Unable to parse file:[" + filename + "]", e);
        }
        result.init();
        return result;
    }

    public static BTree convertFromRaw(String commandText) {
        return convertFromRaw(null, commandText);
    }

    public static BTree convertFromRaw(String source, String commandText) {
        Command command = CommandParser.getCommand(commandText);
        command.setSource(source);
        return BTFactory.convert(command);
    }

    public static BTree convert(Command root) {
        //Root must be BTREE
        BTree rootTree = (BTree) setupCommand(null, root);
        return rootTree;
    }

    public static BTItem setupCommand(BTItem parent, Command currentCommand) {
        BTItem currentItem = getItem(currentCommand);
        //Map Conditions
        for (int i = 0; i < currentCommand.conditions.size; i++) {
            currentItem.conditions.add(getCondition(currentCommand.conditions.get(i)));
        }

        //Validate Single
        if (currentItem instanceof BTSingleChild) {
            BTSingleChild single = (BTSingleChild) currentItem;

            //Deal with condition
            if (currentCommand.subCommands.size > 1) {
                throw new ParseException("Invalid number of sub-commands expected 1 got " + currentCommand.subCommands.size, currentCommand);
            }
            if (currentCommand.subCommands.size == 1) {
                single.child = setupCommand(currentItem, currentCommand.subCommands.get(0));
            }
        } else if (currentItem instanceof BTMultipleChildren) {
            BTMultipleChildren multiple = (BTMultipleChildren) currentItem;
            for (int i = 0; i < currentCommand.subCommands.size; i++) {
                multiple.add(setupCommand(currentItem, currentCommand.subCommands.get(i)));
            }
        }
        currentItem.setParent(parent);
        return currentItem;
    }

    ;

    public static <T extends BTCondition> T getCondition(Command command) {
        return (T) createCondition(command, getConditionClass(command));
    }

    public static <T extends BTItem> T getItem(Command command) {
        T result = (T) createItem(command, getItemClass(command));
        if (result instanceof BTree) {
            BTree tree = (BTree) result;
            if (tree.ref != null) {
                tree.child = getBehaviour(tree.ref);
                tree.child.setParent(tree);
            }
        }
        return result;
    }

    public static <T extends BTCondition> Class<T> getConditionClass(Command command) {
        if (command.isCondition()) {
            switch (command.getCommand()) {
                case HungerCondition.COMMAND_NAME:
                    return (Class<T>) HungerCondition.class;
                case ThirstCondition.COMMAND_NAME:
                    return (Class<T>) ThirstCondition.class;
                case EnergyCondition.COMMAND_NAME:
                    return (Class<T>) EnergyCondition.class;
                case HasTagCondition.COMMAND_NAME:
                    return (Class<T>) HasTagCondition.class;
                case MissingTagCondition.COMMAND_NAME:
                    return (Class<T>) MissingTagCondition.class;
                case HasMemoryCondition.COMMAND_NAME:
                    return (Class<T>) HasMemoryCondition.class;
                case MissingMemoryCondition.COMMAND_NAME:
                    return (Class<T>) MissingMemoryCondition.class;
            }
        }
        throw new RuntimeException("Unknown Condition [" + command.getRawText() + "]");
    }

    public static <T extends BTItem> Class<T> getItemClass(Command command) {
        switch (command.getCommand()) {
            case BrainDumpAction.COMMAND_NAME:
                return (Class<T>) BrainDumpAction.class;
            case BrainForgetAction.COMMAND_NAME:
                return (Class<T>) BrainForgetAction.class;
            case BrainRememberAction.COMMAND_NAME:
                return (Class<T>) BrainRememberAction.class;
            case BrainCopyMemoryAction.COMMAND_NAME:
                return (Class<T>) BrainCopyMemoryAction.class;
            case LogAction.COMMAND_NAME:
                return (Class<T>) LogAction.class;
            case TimeGateAction.COMMAND_NAME:
                return (Class<T>) TimeGateAction.class;
            case BTree.COMMAND_NAME:
                return (Class<T>) BTree.class;
            case BTSequenceAny.COMMAND_NAME:
                return (Class<T>) BTSequenceAny.class;
            case BTSequenceAll.COMMAND_NAME:
                return (Class<T>) BTSequenceAll.class;
            case WanderAction.COMMAND_NAME:
                return (Class<T>) WanderAction.class;
            case CellSearchAction.COMMAND_NAME:
                return (Class<T>) CellSearchAction.class;
            case CellPlanPathAction.COMMAND_NAME:
                return (Class<T>) CellPlanPathAction.class;
            case FailureAction.COMMAND_NAME:
                return (Class<T>) FailureAction.class;
            case SucceedAction.COMMAND_NAME:
                return (Class<T>) SucceedAction.class;
            case CellChangeTypeAction.COMMAND_NAME:
                return (Class<T>) CellChangeTypeAction.class;
            case EntityNotifyDialogAction.COMMAND_NAME:
                return (Class<T>) EntityNotifyDialogAction.class;
            case EntityDrinkAction.COMMAND_NAME:
                return (Class<T>) EntityDrinkAction.class;
            case EntityEatAction.COMMAND_NAME:
                return (Class<T>) EntityEatAction.class;
            case EntitySearchAction.COMMAND_NAME:
                return (Class<T>) EntitySearchAction.class;
            case EntityAddTagAction.COMMAND_NAME:
                return (Class<T>) EntityAddTagAction.class;
            case EntityRemoveTagAction.COMMAND_NAME:
                return (Class<T>) EntityRemoveTagAction.class;
            case EntityExtractAction.COMMAND_NAME:
                return (Class<T>) EntityExtractAction.class;
            case EntityFollowPathAction.COMMAND_NAME:
                return (Class<T>) EntityFollowPathAction.class;
            case EntityDumpTagAction.COMMAND_NAME:
                return (Class<T>) EntityDumpTagAction.class;
            case EntityRestAction.COMMAND_NAME:
                return (Class<T>) EntityRestAction.class;
            case BTLoop.COMMAND_NAME:
                return (Class<T>) BTLoop.class;
            case BrainPickAction.COMMAND_NAME:
                return (Class<T>) BrainPickAction.class;
            case CellMakeRoadAction.COMMAND_NAME:
                return (Class<T>) CellMakeRoadAction.class;





        }
        throw new ParseException("Unknown Command [" + command.getCommand() + "]", command);
    }

    public static <T extends BTItem> T createItem(Command command, Class<T> classItem) {
        T tree = Pools.obtain(classItem);
        tree.setup(command);
        tree.name = command.getName();
        return tree;
    }

    public static <T extends BTCondition> T createCondition(Command command, Class<T> classItem) {
        T tree = Pools.obtain(classItem);
        try {
            tree.parse(command);
        } catch (Exception e) {
            throw new ParseException("Unexpected Error", command, e);
        }
        return tree;
    }
}
