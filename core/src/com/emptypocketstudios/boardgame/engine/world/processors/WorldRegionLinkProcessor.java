package com.emptypocketstudios.boardgame.engine.world.processors;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;
import com.emptypocketstudios.boardgame.engine.world.World;

import java.util.HashMap;

public class WorldRegionLinkProcessor {
    public static final int RESET_LAYER_ID = -1000;

    World world;

    Pool<RegionNode> regionNodePool = new ReflectionPool<RegionNode>(RegionNode.class);
    Pool<Array> regionArrayPool = new ReflectionPool<Array>(Array.class);

    public WorldRegionLinkProcessor(World world) {
        this.world = world;
    }

    protected void addRegionLink(Cell sourceCell, Cell targetCell, ArrayMap<RegionNode, Array<RegionNode>> linkMap) {
        if (sourceCell != null && targetCell != null
        ) {
            Array<RegionNode> links = linkMap.get(sourceCell.region);
            if (links == null) {
                links = regionArrayPool.obtain();
                linkMap.put(regionNodePool.obtain().set(sourceCell.region), links);
            }
            if (!links.contains(targetCell.region, false)) {
                links.add(regionNodePool.obtain().set(targetCell.region));
            }
        }
    }

    public void run() {
        //Free all existing links into the pool
        freeCollection(world.regionLinks);
        freeCollection(world.diagonalRegionLinks);

        Cell start;
        int dx = 0;
        int dy = 0;
        for (int x = 0; x < world.getCellsX(); x++) {
            for (int y = 0; y < world.getCellsY(); y++) {
                //Up
                start = world.getCellByCellId(x, y);
                dx = 0;
                dy = -1;
                if (start.canMove(dx, dy)) {
                    addRegionLink(start, start.getLink(dx, dy), world.regionLinks);
                }

                //Down
                dx = 0;
                dy = 1;
                if (start.canMove(dx, dy)) {
                    addRegionLink(start, start.getLink(dx, dy), world.regionLinks);
                }

                //Left
                dx = -1;
                dy = 0;
                if (start.canMove(dx, dy)) {
                    addRegionLink(start, start.getLink(dx, dy), world.regionLinks);
                }

                //Right
                dx = 1;
                dy = 0;
                if (start.canMove(dx, dy)) {
                    addRegionLink(start, start.getLink(dx, dy), world.regionLinks);
                }

                //Down Left
                dx = -1;
                dy = -1;
                if (start.canMove(dx, dy)) {
                    addRegionLink(start, start.getLink(dx, dy), world.diagonalRegionLinks);
                }

                //Up Right
                dx = 1;
                dy = 1;
                if (start.canMove(dx, dy)) {
                    addRegionLink(start, start.getLink(dx, dy), world.diagonalRegionLinks);
                }

                //Up Left
                dx = -1;
                dy = 1;
                if (start.canMove(dx, dy)) {
                    addRegionLink(start, start.getLink(dx, dy), world.diagonalRegionLinks);
                }

                //Down Right
                dx = 1;
                dy = -1;
                if (start.canMove(dx, dy)) {
                    addRegionLink(start, start.getLink(dx, dy), world.diagonalRegionLinks);
                }
            }
        }
    }

    private void freeCollection(ArrayMap<RegionNode, Array<RegionNode>> regionLinks) {
        for (int i = 0; i < regionLinks.size; i++) {
            RegionNode regionNode = regionLinks.getKeyAt(i);
            Array<RegionNode> links = regionLinks.get(regionNode);

            regionNodePool.free(regionNode);
            regionNodePool.freeAll(links);

            links.clear();
            regionArrayPool.free(links);
        }
        regionLinks.clear();
    }

}
