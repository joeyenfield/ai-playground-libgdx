package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core;

public abstract class BTLeafNode extends BTItem {

    public BTItem getChild(int index) {
        throw new RuntimeException("No children");
    }

    public int getChildCount() {
        return 0;
    }

}
