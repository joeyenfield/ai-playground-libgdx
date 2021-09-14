package com.emptypocketstudios.boardgame.engine.entity.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.btree.BTItem;
import com.emptypocketstudios.boardgame.engine.entity.btree.BTResult;

public class WanderAction extends BTItem {

    public WanderAction(BTItem parent) {
        super(parent);
    }

    @Override
    public BTResult process() {
        return null;
    }

    @Override
    public String commandName() {
        return "WANDER";
    }

    @Override
    public void setup(String command) {

    }

}
