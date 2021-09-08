package com.emptypocketstudios.boardgame.engine.entity.btree;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public abstract class BTSequence extends BTItem {

    Array<BTItem> items = new Array<>();

    public void add(BTItem subItem) {
        items.add(subItem);
    }

    public void remove(BTItem subItem) {
        items.removeIndex(items.indexOf(subItem, true));
    }

    public abstract BTResult process();

    @Override
    public void reset() {
        super.reset();
        Pools.freeAll(items);
        items.clear();
    }
}
