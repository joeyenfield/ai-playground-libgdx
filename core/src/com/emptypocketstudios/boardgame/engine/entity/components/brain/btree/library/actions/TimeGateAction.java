package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

public class TimeGateAction extends BTLeafNode {
    public static final String COMMAND_NAME = "TIME_GATE";
    long interval = 200;
    long nextAllowedTime = 0;

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
        if (nextAllowedTime > System.currentTimeMillis()) {
            return BTResult.FAILURE;
        }
        nextAllowedTime = System.currentTimeMillis() + interval;
        return BTResult.SUCCESS;
    }

    @Override
    public void parse(Command command) {
        interval = command.getArgLong("interval");
    }
}
