package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.manage;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.BrainComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTSingleChild;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

public class BTLoop extends BTSingleChild {
    public static final String COMMAND_NAME = "LOOP";
    public boolean childRunning = false;

    String memoryStartIndex = null;
    String memoryEndIndex = null;

    int startIndex = 0;
    int endIndex = 0;

    int currentIndex = 0;

    String memory;


    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void before() {
        childRunning = false;
        currentIndex = 0;

        if (memoryStartIndex != null) {
            startIndex = entity.getEntityComponent(BrainComponent.class).memory.getInt(memoryStartIndex);
        }

        if (memoryEndIndex != null) {
            endIndex = entity.getEntityComponent(BrainComponent.class).memory.getInt(memoryEndIndex);
        }

    }

    @Override
    public void after() {
    }

    @Override
    public BTResult process() {
        entity.getEntityComponent(BrainComponent.class).memory.store(memory, currentIndex);
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

            if (result == BTResult.SUCCESS) {
                currentIndex++;
                if (currentIndex < endIndex) {
                    result = BTResult.RUNNING;
                }
            }
        }
        return result;
    }

    @Override
    public void parse(Command command) {
        startIndex = (int) command.getArgLong("startIndex", 0);
        endIndex = (int) command.getArgLong("endIndex", 0);

        memoryStartIndex = command.getArg("memoryStartIndex", null);
        memoryEndIndex = command.getArg("memoryEndIndex", null);

        memory = command.getArg("memory");
    }

    @Override
    public void reset() {
        super.reset();
        memoryStartIndex = null;
        memoryEndIndex = null;
    }
}