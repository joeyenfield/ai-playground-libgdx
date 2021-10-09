package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.conditions;

import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTCondition;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTItem;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.ParseException;
import com.emptypocketstudios.boardgame.engine.entity.components.human.LifeComponent;

public class HungerCondition implements BTCondition {
    public static final String COMMAND_NAME = "HUNGER";
    float threshold = 0;

    @Override
    public boolean checkPreCondition(BTItem parent) {
        Entity entity = parent.getEntity();
        LifeComponent lifeComponent = entity.getEntityComponent(LifeComponent.class);
        if (lifeComponent != null) {
            if (lifeComponent.hunger < this.threshold) {
                return true;
            }
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
        this.threshold = command.getArgFloat("threshold", 10f);
    }
}
