package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.BrainComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

public class EntityRemoveTagAction extends BTLeafNode {
    public static final String COMMAND_NAME = "ENTITY_REMOVE_TAG";
    String tag;
    String memoryEntityName;

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
        Entity entityToTag = null;
        if (memoryEntityName == null) {
            entityToTag = this.entity;
        } else {
            BrainComponent brain = entity.getEntityComponent(BrainComponent.class);
            String searchName = brain.memory.getString(memoryEntityName);
            entityToTag = this.entity.world.getEntityByName(searchName);
        }

        if (entityToTag == null || !entityToTag.hasTag(tag)) {
            return BTResult.FAILURE;
        }
        entityToTag.removeTag(tag);
        return BTResult.SUCCESS;
    }

    @Override
    public void parse(Command command) {
        tag = command.getArg("tag");
        memoryEntityName = command.getArg("memoryEntityName", null);
    }
}
