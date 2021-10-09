package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.manage;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTSingleChild;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

public class BTree extends BTSingleChild {
    public static final String COMMAND_NAME = "BTREE";
    public boolean childRunning = false;
    public String ref = null;

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void before() {
        childRunning = false;
    }

    @Override
    public void after() {
    }

    @Override
    public BTResult process() {
        if (child == null) {
            printStructure();
            return BTResult.SUCCESS;
        }
        if (!childRunning) {
            child.before();
            childRunning = true;
        }
        BTResult result = child.tick();
        if (result != BTResult.RUNNING) {
            child.after();
            childRunning = false;
        }
        return result;
    }

    @Override
    public void parse(Command command) {
        this.name = command.getName();
        this.ref = command.getArg("ref", null);
    }

    @Override
    public void reset() {
        super.reset();
        name = null;
    }
}
