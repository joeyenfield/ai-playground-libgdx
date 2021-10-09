package com.emptypocketstudios.boardgame.engine.world.processors.entitysearch;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import java.util.Objects;

public abstract class EntityMultipleFilter extends EntityFilter {

    Array<EntityFilter> filters = new Array<>();

    public EntityMultipleFilter add(EntityFilter filter) {
        this.filters.add(filter);
        return this;
    }

    public EntityMultipleFilter remove(EntityFilter filter) {
        this.filters.removeValue(filter, false);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityMultipleFilter that = (EntityMultipleFilter) o;
        return Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filters);
    }

    @Override
    public void dispose() {
        Pools.freeAll(filters);
    }
}
