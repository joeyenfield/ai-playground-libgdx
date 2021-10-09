package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.BrainComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

public class EntityDumpTagAction extends BTLeafNode {
    public static final String COMMAND_NAME = "ENTITY_DUMP_TAG";
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

        entityToTag.dumpTags();

        return BTResult.SUCCESS;
    }

    @Override
    public void parse(Command command) {
        memoryEntityName = command.getArg("memoryEntityName", null);
    }
}
