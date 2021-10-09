package com.emptypocketstudios.boardgame.engine.world.processors.entitysearch;

import com.emptypocketstudios.boardgame.engine.entity.Entity;

public class EntityDistanceFilter extends EntityFilter {

    float x;
    float y;
    float maxRange;

    public EntityDistanceFilter() {
    }

    public EntityDistanceFilter setMaxRange(float x, float y, float maxRange) {
        this.x = x;
        this.y = y;
        this.maxRange = maxRange;
        return this;
    }

    @Override
    public boolean match(Entity e) {
        if (e.pos.dst2(x, y) < maxRange * maxRange) {
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        x = 0;
        y = 0;
        maxRange = 0;
    }
}
