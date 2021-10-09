package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.conditions;

import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.BrainComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTCondition;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTItem;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.ParseException;

public class HasMemoryCondition implements BTCondition {
    public static final String COMMAND_NAME = "HAS_MEMORY";
    String memoryName;

    @Override
    public boolean checkPreCondition(BTItem parent) {
        Entity entity = parent.getEntity();
        BrainComponent brain = entity.getEntityComponent(BrainComponent.class);
        if(brain != null && brain.memory.hasMemory(memoryName)){
            return true;
        }
        return false;
    }

    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void parse(Command command) {
        if (!getCommandName().equalsIgnoreCase(command.getCommand())) {
            throw new ParseException("Unexpected Command [" + command.getCommand() + " - " + getCommandName() + "]", command);
        }
        this.memoryName = command.getArg("memoryName");
    }
}
