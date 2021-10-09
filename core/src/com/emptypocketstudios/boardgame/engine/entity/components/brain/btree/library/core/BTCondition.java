package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

public interface BTCondition {
    public String getCommandName();
    public boolean checkPreCondition(BTItem parent);
    public void parse(Command command);
}
