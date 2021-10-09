package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;

public class CellMakeRoadAction extends BTLeafNode {
    public static final String COMMAND_NAME = "BUILD_ROAD";
    long delay = 200;
    long completionTime = 0;

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void before() {
        completionTime = System.currentTimeMillis() + delay;
    }

    @Override
    public void after() {
    }

    @Override
    public BTResult process() {
        if (completionTime > System.currentTimeMillis()) {
            return BTResult.RUNNING;
        }
        Cell cell = entity.world.getCellAtWorldPosition(entity.pos);
        if (cell != null) {
            cell.isRoad = true;
            return BTResult.SUCCESS;
        }
        return BTResult.FAILURE;
    }

    @Override
    public void parse(Command command) {
        delay = Long.parseLong(command.getArg("delay", "0"));
    }
}
