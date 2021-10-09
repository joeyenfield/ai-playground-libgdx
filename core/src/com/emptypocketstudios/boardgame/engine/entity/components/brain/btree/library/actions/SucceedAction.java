package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

public class SucceedAction extends BTLeafNode {
    public static final String COMMAND_NAME = "SUCCESS";
    long delay = 200;
    long completionTime = 0;

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void before() {
        completionTime = System.currentTimeMillis() + delay;
    }

    @Override
    public void after() {
    }

    @Override
    public BTResult process() {
        if (completionTime > System.currentTimeMillis()) {
            return BTResult.RUNNING;
        }
        return BTResult.SUCCESS;
    }

    @Override
    public void parse(Command command) {
        delay = Long.parseLong(command.getArg("delay", "0"));
    }
}
