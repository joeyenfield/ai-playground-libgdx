package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.conditions;

import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTCondition;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTItem;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.ParseException;

public class HasTagCondition implements BTCondition {
    public static final String COMMAND_NAME = "HAS_TAG";
    String tag;

    @Override
    public boolean checkPreCondition(BTItem parent) {
        Entity entity = parent.getEntity();
        return entity.hasTag(tag);
    }

    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void parse(Command command) {
        if (!getCommandName().equalsIgnoreCase(command.getCommand())) {
            throw new ParseException("Unexpected Command [" + command.getCommand() + " - " + getCommandName() + "]", command);
        }
        this.tag = command.getArg("tag");
    }
}
