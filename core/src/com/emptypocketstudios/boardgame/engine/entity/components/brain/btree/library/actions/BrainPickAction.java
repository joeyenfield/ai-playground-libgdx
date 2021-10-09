package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.badlogic.gdx.utils.Array;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.BrainComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingResponse;
import com.emptypocketstudios.boardgame.engine.world.Cell;

public class BrainPickAction extends BTLeafNode {
    public static final String COMMAND_NAME = "BRAIN_PICK_DATA";
    String sourceMemory;
    String sourceType;
    String sourceData;
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
        if ("ARRAY_ELEMENT".equalsIgnoreCase(sourceType)) {
            Array source = (Array) brain.memory.getObject(sourceMemory);
            int index = brain.memory.getInt(sourceData);
            brain.memory.store(memory, source.get(index));
        } else if ("ARRAY_SIZE".equalsIgnoreCase(sourceType)) {
            Array source = (Array) brain.memory.getObject(sourceMemory);
            brain.memory.store(memory, source.size);
        } else if ("EXTRACT_PATH".equalsIgnoreCase(sourceType)) {
            PathFindingResponse source = (PathFindingResponse) brain.memory.getObject(sourceMemory);
            brain.memory.store(memory, source.path);
        } else if ("CELL_POS".equalsIgnoreCase(sourceType)) {
            Cell source = (Cell) brain.memory.getObject(sourceMemory);
            brain.memory.store(memory, source.pos);
        } else {
            return BTResult.FAILURE;
        }
        return BTResult.SUCCESS;
    }

    @Override
    public void parse(Command command) {
        memory = command.getArg("memory");
        sourceMemory = command.getArg("sourceMemory");
        sourceType = command.getArg("sourceType");
        sourceData = command.getArg("sourceData", null);
    }

}
