package com.emptypocketstudios.boardgame.engine.entity.components.resource;

import com.badlogic.gdx.utils.Pool;

public class ResourceStore implements Pool.Poolable {
    Resource type;
    int capacity;
    int current;

    public ResourceStore() {
    }

    public ResourceStore set(Resource type, int capacity, int current) {
        this.type = type;
        this.capacity = capacity;
        this.current = current;
        return this;
    }

    @Override
    public void reset() {
    }
}
