package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.BrainComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

public class EntityExtractAction extends BTLeafNode {
    public static final String COMMAND_NAME = "ENTITY_EXTRACT";
    String memoryEntityName;
    String field;
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
        Entity targetEntity = null;
        if (memoryEntityName == null) {
            targetEntity = this.entity;
        } else {
            BrainComponent brain = entity.getEntityComponent(BrainComponent.class);
            String searchName = brain.memory.getString(memoryEntityName);
            targetEntity = this.entity.world.getEntityByName(searchName);
        }

        if (targetEntity == null) {
            return BTResult.FAILURE;
        }
        if ("POS".equalsIgnoreCase(field)) {
            BrainComponent brain = entity.getEntityComponent(BrainComponent.class);
            brain.memory.store(memory, targetEntity.pos);
        } else {
            throw new RuntimeException("Failure");
        }
        return BTResult.SUCCESS;
    }

    @Override
    public void parse(Command command) {
        field = command.getArg("field");
        memory = command.getArg("memory");
        memoryEntityName = command.getArg("memoryEntityName", null);
    }
}
