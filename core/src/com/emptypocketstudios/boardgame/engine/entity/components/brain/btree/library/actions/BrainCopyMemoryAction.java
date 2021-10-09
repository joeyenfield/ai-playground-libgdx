package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.BrainComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

public class BrainCopyMemoryAction extends BTLeafNode {
    public static final String COMMAND_NAME = "BRAIN_COPY";

    String memoryEntityName;
    String memory;
    String otherMemory;

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
        String searchName = brain.memory.getString(memoryEntityName);
        Entity targetEntity = this.entity.world.getEntityByName(searchName);
        BrainComponent targetBrain = targetEntity.getEntityComponent(BrainComponent.class);
        brain.memory.store(memory, targetBrain.memory.getObject(otherMemory));
        return BTResult.SUCCESS;
    }

    @Override
    public void parse(Command command) {
        memory = command.getArg("memory");
        otherMemory = command.getArg("otherMemory");
        memoryEntityName = command.getArg("memoryEntityName");
    }

}
