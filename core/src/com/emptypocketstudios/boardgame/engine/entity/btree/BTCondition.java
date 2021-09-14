package com.emptypocketstudios.boardgame.engine.entity.btree;

import com.emptypocketstudios.boardgame.engine.entity.Entity;

public interface BTCondition {
    public boolean checkPreCondition(BTItem parent);
    public void parse(String command);
}
