package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;
import com.emptypocketstudios.boardgame.engine.entity.components.human.LifeComponent;
import com.emptypocketstudios.boardgame.engine.world.Cell;

public class EntityEatAction extends BTLeafNode {
    public static final String COMMAND_NAME = "EAT";
    float restore = 10;

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
        Cell cell = entity.getCurrentCell();
        LifeComponent life = entity.getEntityComponent(LifeComponent.class);
        life.hunger += restore;
        return BTResult.SUCCESS;
    }

    @Override
    public void parse(Command command) {
        restore = command.getArgFloat("restore", 10);
    }

}
