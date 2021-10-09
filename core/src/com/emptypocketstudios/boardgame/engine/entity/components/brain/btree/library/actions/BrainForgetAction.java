package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.BrainComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

public class BrainForgetAction extends BTLeafNode {
    public static final String COMMAND_NAME = "BRAIN_FORGET";

    String memoryEntityName;
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
        Entity entityToDump = null;
        if (memoryEntityName == null) {
            entityToDump = this.entity;
        } else {
            BrainComponent brain = entity.getEntityComponent(BrainComponent.class);
            String searchName = brain.memory.getString(memoryEntityName);
            entityToDump = this.entity.world.getEntityByName(searchName);
        }

        BrainComponent brain = entityToDump.getEntityComponent(BrainComponent.class);
        brain.memory.forget(memory);
        return BTResult.SUCCESS;
    }

    @Override
    public void parse(Command command) {
        memory = command.getArg("memory");
        memoryEntityName = command.getArg("memoryEntityName", null);
    }

}
