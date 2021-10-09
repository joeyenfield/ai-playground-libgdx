package com.emptypocketstudios.boardgame.engine.world.processors;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;
import com.emptypocketstudios.boardgame.engine.world.processors.entitysearch.EntityAndFilter;
import com.emptypocketstudios.boardgame.engine.world.processors.entitysearch.EntityDistanceFilter;
import com.emptypocketstudios.boardgame.engine.world.processors.entitysearch.EntityMultipleFilter;

public class WorldEntitySearch {

    World world;

    public WorldEntitySearch(World world) {
        this.world = world;
    }

    public Entity pickEntity(float x, float y, SearchType search, Array<Entity> entity) {
        Entity selectedEntity = null;
        if (search == SearchType.NEAREST) {
            float distance = Float.MAX_VALUE;
            for (int i = 0; i < entity.size; i++) {
                Entity ent = entity.get(i);
                float dst = ent.pos.dst2(x, y);
                if (selectedEntity == null || dst < distance) {
                    distance = dst;
                    selectedEntity = ent;
                }
            }
        } else if (search == SearchType.RANDOM) {
            selectedEntity = world.engine.random.random(entity);
        }
        return selectedEntity;
    }

    public void searchEntitiesByFilter(float x, float y, float range, String tags[], Array<Entity> result) {
        GridPoint2 p1 = Pools.obtain(GridPoint2.class);
        GridPoint2 p2 = Pools.obtain(GridPoint2.class);
        WorldChunk c1 = world.getChunkAtWorldPosition(x - range, y - range, true);
        WorldChunk c2 = world.getChunkAtWorldPosition(x + range, y + range, true);
        p1.set(c1.chunkId);
        p2.set(c2.chunkId);
        EntityMultipleFilter searchFilter = Pools.obtain(EntityAndFilter.class)
                .add(Pools.obtain(EntityDistanceFilter.class).setMaxRange(x, y, range));

        for (int cX = p1.x; cX <= p2.x; cX++) {
            for (int cY = p1.y; cY <= p2.y; cY++) {
                WorldChunk chunk = world.getChunkByChunkId(cX, cY);
                if (tags == null) {
                    // No Tags
                    chunk.getFilteredEntities(searchFilter, result);
                } else {
                    // Multiple tags
                    for (String tag : tags) {
                        chunk.getFilteredEntities(tag, searchFilter, result);
                    }
                }
            }
        }

        Pools.free(p1);
        Pools.free(p2);
        Pools.free(searchFilter);
    }
}
