package com.emptypocketstudios.boardgame.engine.world.processors.entitysearch;

import com.emptypocketstudios.boardgame.engine.entity.Entity;

public class EntityOrFilter extends EntityMultipleFilter {

    @Override
    public boolean match(Entity e) {
        for (int i = 0; i < filters.size; i++) {
            if (filters.get(i).match(e)) {
                return true;
            }
        }
        return false;
    }

}
