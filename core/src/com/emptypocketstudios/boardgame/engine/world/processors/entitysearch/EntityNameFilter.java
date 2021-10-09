package com.emptypocketstudios.boardgame.engine.world.processors.entitysearch;

import com.emptypocketstudios.boardgame.engine.entity.Entity;

import java.util.Objects;

public class EntityNameFilter extends EntityFilter {

    String name = null;

    public EntityNameFilter() {
    }

    public EntityNameFilter setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean match(Entity e) {
        if (name != null && e.name.equalsIgnoreCase(name)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityNameFilter that = (EntityNameFilter) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public void dispose() {
        name = null;
    }
}
