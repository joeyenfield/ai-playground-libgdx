package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;

public class CellChangeTypeAction extends BTLeafNode {
    public static final String COMMAND_NAME = "CHANGE_CELL";
    long delay = 200;
    long completionTime = 0;

    CellType oldCellType = CellType.EMPTY;
    CellType newCellType = CellType.EMPTY;
    boolean copyVariant = false;
    int variant = 0;

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
            if (cell.type == oldCellType) {
                int newVariant = variant;
                if (copyVariant) {
                    newVariant = cell.typeVariant;
                }
                cell.setType(newCellType, newVariant);
                return BTResult.SUCCESS;
            }
        }
        return BTResult.FAILURE;
    }

    @Override
    public void parse(Command command) {
        delay = Long.parseLong(command.getArg("delay", "0"));
        oldCellType = CellType.valueOf(command.getArg("oldCellType"));
        newCellType = CellType.valueOf(command.getArg("newCellType"));
        copyVariant = command.getArgBoolean("copyVariant", false);
        variant = (int) command.getArgLong("variant", 0);
    }
}
