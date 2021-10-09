package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

public class WanderAction extends BTLeafNode {
    public static final String COMMAND_NAME = "WANDER";
    long wanderTime = 1;
    boolean wandering = false;
    long completionTime = 0;

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
        //Assume starting to wander
        if (wandering == false) {
            wandering = true;
            completionTime = System.currentTimeMillis() + wanderTime;
        }

        if (completionTime < System.currentTimeMillis()) {
            return BTResult.SUCCESS;
        }


        return BTResult.RUNNING;
    }

    @Override
    public void parse(Command command) {
        wanderTime = Long.parseLong(command.getArg("wanderTime", "5000"));
    }

}
