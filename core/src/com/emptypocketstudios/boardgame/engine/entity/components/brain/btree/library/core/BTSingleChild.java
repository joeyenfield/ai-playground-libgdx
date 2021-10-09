package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core;

import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.entity.Entity;

public abstract class BTSingleChild extends BTItem {

    public BTItem child = null;

    public BTItem getChild(int index) {
        if (index != 0) {
            throw new RuntimeException("Only Single Child supported");
        }
        return child;
    }

    public int getChildCount() {
        if (child == null) {
            return 0;
        }
        return 1;
    }

    @Override
    public void setEntity(Entity entity) {
        super.setEntity(entity);
        if(child != null) {
            child.setEntity(entity);
        }
    }

    @Override
    public void init() {
        super.init();
        if(child != null) {
            child.init();
        }
    }

    @Override
    public void printStructure() {
        super.printStructure();
        if(child != null) {
            child.printStructure();
        }
    }

    @Override
    public void reset() {
        super.reset();
        if(child != null) {
            Pools.free(child);
            child = null;
        }
    }
}
