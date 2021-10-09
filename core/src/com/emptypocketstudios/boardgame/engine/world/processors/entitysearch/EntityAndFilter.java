package com.emptypocketstudios.boardgame.engine.world.processors.entitysearch;

import com.emptypocketstudios.boardgame.engine.entity.Entity;

public class EntityAndFilter extends EntityMultipleFilter {

    @Override
    public boolean match(Entity e) {
        for (int i = 0; i < filters.size; i++) {
            if (!filters.get(i).match(e)) {
                return false;
            }
        }
        return true;
    }
}
