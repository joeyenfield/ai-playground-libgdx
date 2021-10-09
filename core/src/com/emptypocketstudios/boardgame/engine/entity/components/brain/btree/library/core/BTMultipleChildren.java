package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.entity.Entity;

public abstract class BTMultipleChildren extends BTItem {

    protected Array<BTItem> children = new Array<>();

    public BTItem getChild(int index) {
        return children.get(index);
    }

    public int getChildCount() {
        return children.size;
    }

    public void add(BTItem subItem) {
        children.add(subItem);
    }

    public void remove(BTItem subItem) {
        children.removeIndex(children.indexOf(subItem, true));
    }

    @Override
    public void setEntity(Entity entity) {
        super.setEntity(entity);
        for (int i = 0; i < children.size; i++) {
            children.get(i).setEntity(entity);
        }
    }

    @Override
    public void init() {
        super.init();
        for (int i = 0; i < children.size; i++) {
            children.get(i).init();
        }
    }

    @Override
    public void printStructure() {
        super.printStructure();
        for (int i = 0; i < children.size; i++) {
            children.get(i).printStructure();
        }
    }

    @Override
    public void reset() {
        super.reset();
        Pools.freeAll(children);
        children.clear();
    }
}
