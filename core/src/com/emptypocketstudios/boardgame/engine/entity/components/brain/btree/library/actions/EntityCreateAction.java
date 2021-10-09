package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.EntityType;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;
import com.emptypocketstudios.boardgame.engine.entity.components.building.BuildingType;
import com.emptypocketstudios.boardgame.engine.entity.components.human.HumanType;

public class EntityCreateAction extends BTLeafNode {
    public static final String COMMAND_NAME = "ENTITY_CREATE";
    String tag;
    EntityType entityType;
    BuildingType buildingType;
    HumanType humanType;
    String entityBaseName;

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
//        Entity createdEntity = entity.world.createEntity(entityType, entityBaseName);
//        if (entityToTag == null) {
//            return BTResult.FAILURE;
//        }
//        entityToTag.addTag(tag);
        return BTResult.SUCCESS;
    }

    @Override
    public void parse(Command command) {
        tag = command.getArg("tag");
//        memoryEntityName = command.getArg(memoryEntityName, null);
    }
}
