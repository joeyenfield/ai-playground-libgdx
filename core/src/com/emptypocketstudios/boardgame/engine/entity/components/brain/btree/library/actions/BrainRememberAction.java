package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.BrainComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

public class BrainRememberAction extends BTLeafNode {
    public static final String COMMAND_NAME = "BRAIN_REMEMBER";

    String memoryEntityName;
    String memory;
    String value;

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
        Entity targetEntity = null;
        if (memoryEntityName == null) {
            targetEntity = this.entity;
        } else {
            BrainComponent brain = entity.getEntityComponent(BrainComponent.class);
            String searchName = brain.memory.getString(memoryEntityName);
            targetEntity = this.entity.world.getEntityByName(searchName);
        }
        BrainComponent brain = targetEntity.getEntityComponent(BrainComponent.class);
        brain.memory.store(memory, value);
        return BTResult.SUCCESS;
    }

    @Override
    public void parse(Command command) {
        memory = command.getArg("memory");
        value = command.getArg("value");
        memoryEntityName = command.getArg("memoryEntityName", null);
    }

}
