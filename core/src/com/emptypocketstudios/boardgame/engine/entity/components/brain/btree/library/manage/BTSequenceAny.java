package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.manage;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTItem;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTMultipleChildren;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

public class BTSequenceAny extends BTMultipleChildren {
    public static final String COMMAND_NAME = "ANY";

    int currentIndex = 0;
    BTItem lastItem = null;
    boolean continueOnSuccess = false;
    boolean hadSuccess = false;

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void before() {
        currentIndex = 0;
        lastItem = null;
        hadSuccess = false;
    }

    @Override
    public void after() {

    }

    private BTItem getCurrentItem() {
        BTItem current = children.get(currentIndex);
        if (current != lastItem) {
            current.before();
            lastItem = current;
        }
        return current;
    }

    @Override
    public BTResult process() {
        while (currentIndex < children.size) {
            if (debugTrace) logDebug("Running[" + currentIndex + " of " + children.size + "]");
            BTItem child = getCurrentItem();
            BTResult result = child.tick();
            if (result != BTResult.RUNNING) {
                child.after();
            }
            if (result == BTResult.FAILURE) {
                currentIndex++;
                if (debugTrace) logDebug("Next[" + currentIndex + " of " + children.size + "]");
            } else if (result == BTResult.SUCCESS) {
                if (continueOnSuccess) {
                    currentIndex++;
                    hadSuccess = true;
                } else {
                    return BTResult.SUCCESS;
                }
            } else if (result == BTResult.RUNNING) {
                return BTResult.RUNNING;
            }
        }
        if (hadSuccess) {
            return BTResult.SUCCESS;
        }
        return BTResult.FAILURE;
    }

    @Override
    public void parse(Command command) {
        continueOnSuccess = command.getArgBoolean("continueOnSuccess", false);
    }

    @Override
    public void reset() {
        super.reset();
    }
}
