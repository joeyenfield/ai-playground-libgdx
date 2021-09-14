package com.emptypocketstudios.boardgame.engine.entity.btree;

import com.badlogic.gdx.utils.Pool;
import com.emptypocketstudios.boardgame.engine.entity.Entity;

import java.util.HashMap;

public abstract class BTItem implements Pool.Poolable {
    public String name = null;
    public BTItem parent = null;
    public BTCondition condition = null;
    public HashMap<String, Object> memory;

    public BTItem(HashMap<String, Object> memory) {
        this.memory = memory;
    }

    public BTItem(BTItem parent) {
        this.parent = parent;
        this.memory = parent.memory;
    }

    public BTResult tick() {
        // Checks if pre-condition is met before entering
        if (condition != null && !condition.checkPreCondition(this)) {
            return BTResult.FAILURE;
        }
        return process();
    }

    public abstract BTResult process();

    public abstract String commandName();

    public abstract void setup(String command);

    @Override
    public void reset() {
        condition = null;
        parent = null;
    }
}
