package com.emptypocketstudios.boardgame.engine.world.processors.entitysearch;

import com.emptypocketstudios.boardgame.engine.entity.Entity;

import java.util.Objects;

public class EntityTagFilter extends EntityFilter {

    String tag = null;

    public EntityTagFilter() {
    }

    public EntityTagFilter setTag(String tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public boolean match(Entity e) {
        if (tag != null && e.hasTag(tag)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityTagFilter that = (EntityTagFilter) o;
        return Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }

    @Override
    public void dispose() {
        tag = null;
    }
}
