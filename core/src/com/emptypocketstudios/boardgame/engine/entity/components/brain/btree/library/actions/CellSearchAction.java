package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.BrainComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.ParseException;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;
import com.emptypocketstudios.boardgame.engine.world.processors.SearchType;

public class CellSearchAction extends BTLeafNode {
    public static final String COMMAND_NAME = "CELL_SEARCH";
    float range;
    SearchType searchType;
    CellType type;
    String memory;

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void before() {
    }

    @Override
    public void after() {
    }

    @Override
    public BTResult process() {
        BrainComponent brain = entity.getEntityComponent(BrainComponent.class);
        Cell cell = entity.world.getCellAtWorldPosition(entity.pos);
        Cell targetCell = null;

        if (cell == null) {
            return BTResult.FAILURE;
        }

        //Cells to world distance
        float rangeWorld = entity.world.engine.config.cellSize * range;

        switch (searchType) {
            case NEAR_SPIRAL:
                targetCell = entity.world.cellSearcher.spiralSearch(cell, rangeWorld, type);
                break;
            case RANDOM:
                targetCell = entity.world.cellSearcher.randomSearchScan(cell, rangeWorld, type);
                break;
            default:
                throw new RuntimeException("Not yet programmed " + searchType);
        }
        if (targetCell == null) {
            return BTResult.FAILURE;
        }
        brain.memory.store(memory, targetCell.pos);
        return BTResult.SUCCESS;

    }

    @Override
    public void parse(Command command) {
        range = command.getArgFloat("range", 5f);
        searchType = SearchType.valueOf(command.getArg("searchType", SearchType.NEAR_SPIRAL.name()));
        memory = command.getArg("memory");
        String typeName = command.getArg("type");
        try {
            type = CellType.valueOf(typeName);
        } catch (Exception e) {
            type = CellType.EMPTY;
        }
        if (type == CellType.EMPTY) {
            throw new ParseException("Unknown type :'" + typeName + "'", command);
        }

    }

}
