package com.emptypocketstudios.boardgame.engine.entity.btree;

import com.badlogic.gdx.utils.Pool;

public abstract class BTItem implements Pool.Poolable {
    public abstract BTResult process();

    @Override
    public void reset() {
    }
}
