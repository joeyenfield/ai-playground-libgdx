package com.emptypocketstudios.boardgame.engine.world.processors.entitysearch;

import com.badlogic.gdx.utils.Disposable;
import com.emptypocketstudios.boardgame.engine.entity.Entity;

public abstract class EntityFilter implements Disposable {
    public abstract boolean match(Entity e);
}
