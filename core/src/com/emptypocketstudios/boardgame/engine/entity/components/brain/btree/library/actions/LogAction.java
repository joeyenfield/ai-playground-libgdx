package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

public class LogAction extends BTLeafNode {
    public static final String COMMAND_NAME = "LOG";
    String message;

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
        if (entity.world.engine.config.enableFullBTreeLogging || debugLog) {
            entity.world.engine.log("LogAction", entity, message);
        }
        return BTResult.SUCCESS;
    }

    @Override
    public void parse(Command command) {
        message = command.getArg("message");
    }

}
