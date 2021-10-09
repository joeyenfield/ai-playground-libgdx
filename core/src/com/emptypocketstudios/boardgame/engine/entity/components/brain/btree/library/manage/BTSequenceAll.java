package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.manage;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTItem;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTMultipleChildren;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

public class BTSequenceAll extends BTMultipleChildren {
    public static final String COMMAND_NAME = "ALL";

    int currentIndex = 0;
    BTItem lastItem = null;
    boolean continueOnFailure = false;
    boolean hadFailure = false;

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void before() {
        currentIndex = 0;
        lastItem = null;
        hadFailure = false;
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
            BTItem child = getCurrentItem();
            BTResult result = child.tick();
            if (result != BTResult.RUNNING) {
                child.after();
            }
            if (result == BTResult.SUCCESS) {
                currentIndex++;
            } else if (result == BTResult.FAILURE) {
                if (continueOnFailure) {
                    hadFailure = true;
                    currentIndex++;
                } else {
                    return BTResult.FAILURE;
                }
            } else if (result == BTResult.RUNNING) {
                return BTResult.RUNNING;
            }
        }
        if (hadFailure) {
            return BTResult.FAILURE;
        }
        return BTResult.SUCCESS;
    }

    @Override
    public void parse(Command command) {
        continueOnFailure = command.getArgBoolean("continueOnFailure", false);
    }

    @Override
    public void reset() {
        super.reset();
        currentIndex = 0;
    }
}
