package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.badlogic.gdx.utils.Array;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.BrainComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.processors.SearchType;

public class EntitySearchAction extends BTLeafNode {
    public static final String COMMAND_NAME = "ENTITY_SEARCH";

    float range;
    SearchType searchType;
    String tag[];
    String memory;

    Array<Entity> entities = new Array<>();

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
        if (cell == null) {
            return BTResult.FAILURE;
        }
        float rangeWorld = entity.world.engine.config.cellSize * range;

        entity.world.entitySearcher.searchEntitiesByFilter(entity.pos.x, entity.pos.y, rangeWorld, tag, entities);
        Entity targetEntity = entity.world.entitySearcher.pickEntity(entity.pos.x, entity.pos.y, searchType, entities);
        entities.clear();

        if (targetEntity == null) {
            return BTResult.FAILURE;
        }


        brain.memory.store(memory, targetEntity.name);
        return BTResult.SUCCESS;

    }

    @Override
    public void parse(Command command) {
        memory = command.getArg("memory");
        range = command.getArgFloat("range", 60);
        tag = command.getArg("tag").split(";");
        searchType = SearchType.valueOf(command.getArg("searchType"));
    }
}
