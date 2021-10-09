package com.emptypocketstudios.boardgame.engine.world;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.world.processors.WorldChunkCellLinkProcessor;
import com.emptypocketstudios.boardgame.engine.world.processors.WorldChunkRegionProcessor;
import com.emptypocketstudios.boardgame.engine.world.processors.entitysearch.EntityFilter;

import java.util.HashMap;

public class WorldChunk implements Pool.Poolable {
    public World world;
    public GridPoint2 chunkId = new GridPoint2();
    public Rectangle boundary = new Rectangle();

    public int numCellsX = 1;
    public int numCellsY = 1;
    public Cell[][] cells;

    public Array<Entity> entities = new Array<>();
    public ArrayMap<String, Array<Entity>> taggedEntities = new ArrayMap<>();

    public boolean updateRegionsRequired = true;

    public WorldChunkRegionProcessor worldChunkRegionProcessor;
    public WorldChunkCellLinkProcessor worldChunkCellLinkProcessor;

    public WorldChunk() {
        this.worldChunkCellLinkProcessor = new WorldChunkCellLinkProcessor(this);
        this.worldChunkRegionProcessor = new WorldChunkRegionProcessor(this);
    }

    public void init(World world, Rectangle region, int chunkX, int chunkY, int numCellsX, int numCellsY) {
        this.world = world;
        this.boundary.set(region);
        this.chunkId.set(chunkX, chunkY);
        this.numCellsX = numCellsX;
        this.numCellsY = numCellsY;

        this.entities.clear();

        if (cells == null || cells.length != numCellsX || cells[0].length != numCellsY) {
            cells = new Cell[numCellsX][numCellsY];
            for (int x = 0; x < numCellsX; x++) {
                for (int y = 0; y < numCellsY; y++) {
                    cells[x][y] = Pools.obtain(Cell.class);
                    cells[x][y].chunk = this;
                }
            }
        }

        Rectangle subRegion = Pools.obtain(Rectangle.class);
        GridPoint2 cellId = Pools.obtain(GridPoint2.class);

        subRegion.width = region.width / numCellsX;
        subRegion.height = region.height / numCellsY;
        for (int x = 0; x < numCellsX; x++) {
            for (int y = 0; y < numCellsY; y++) {
                subRegion.x = region.x + subRegion.width * x;
                subRegion.y = region.y + subRegion.height * y;
                cellId.x = numCellsX * chunkX + x;
                cellId.y = numCellsY * chunkY + y;
                cells[x][y].init(chunkId, cellId, subRegion, CellType.GRASS);
            }
        }

        updateRegionsRequired = true;

        Pools.free(subRegion);
        Pools.free(cellId);
    }

    public Cell getCellByCellId(int x, int y) {
        //Offset the top cells
        x = x - chunkId.x * numCellsX;
        y = y - chunkId.y * numCellsY;
        return getCellByIndex(x, y);
    }

    /**
     * Gets the cell by the id within the internal 2d array
     */
    public Cell getCellByIndex(int x, int y) {
        if (x < 0 || y < 0 || x >= numCellsX || y >= numCellsY) {
            return null;
        }
        return cells[x][y];
    }

    public void setupInternalLinks() {
        this.worldChunkCellLinkProcessor.process();
    }

    @Override
    public void reset() {
        world = null;
        for (int x = 0; x < numCellsX; x++) {
            for (int y = 0; y < numCellsY; y++) {
                Cell c = cells[x][y];
                cells[x][y] = null;
                Pools.free(c);
            }
        }
    }

    public void updateRegions() {
        if (updateRegionsRequired) {
            this.worldChunkRegionProcessor.process();
            this.updateRegionsRequired = false;
        }
    }

    public void update(float delta) {
        for (int i = 0; i < entities.size; i++) {
            Entity e = entities.get(i);
            e.update(delta);
        }

        for (int i = entities.size - 1; i >= 0; i--) {
            Entity e = entities.get(i);
            if (!boundary.contains(e.pos)) {
                WorldChunk newChunk = world.getChunkAtWorldPosition(e.pos);
                if (newChunk != null) {
                    removeEntity(e);
                    newChunk.addEntity(e);
                }
            }
        }
    }

    public Array<Entity> getEntitiesByTag(String tag) {
        if (!taggedEntities.containsKey(tag)) {
            Array<Entity> entities = new Array(1024);
            taggedEntities.put(tag, entities);
        }
        return taggedEntities.get(tag);
    }

    public void addEntity(Entity e) {
        entities.add(e);
        for (int i = 0; i < e.getTags().size; i++) {
            addEntityTags(e.getTags().get(i), e);
        }
    }

    public void addEntityTags(String tag, Entity e) {
        Array<Entity> entities = getEntitiesByTag(tag);
        if (!entities.contains(e, false)) {
            entities.add(e);
        } else {
            world.engine.log("WorldChunk.addEntityTags", e, " already has tag:" + tag);
        }
    }

    public void removeEntity(Entity e) {
        entities.removeValue(e, false);
        for (int i = 0; i < e.getTags().size; i++) {
            removeEntityTags(e.getTags().get(i), e);
        }
    }

    public void removeEntityTags(String tag, Entity e) {
        Array<Entity> entities = getEntitiesByTag(tag);
        if (entities.contains(e, false)) {
            entities.removeValue(e, false);
        } else {
            world.engine.log("WorldChunk.removeEntityTags", e, " never had tag:" + tag);
        }
    }


    public void fillAllCells(CellType type) {
        for (int x = 0; x < numCellsX; x++) {
            for (int y = 0; y < numCellsY; y++) {
                Cell c = cells[x][y];
                cells[x][y].setType(type);
                cells[x][y].isRoad = false;
                updateRegionsRequired = true;
            }
        }
    }

    public void getFilteredEntities(String tag, EntityFilter searchFilter, Array<Entity> result) {
        Array<Entity> entities = getEntitiesByTag(tag);
        filterEntities(entities, searchFilter, result);
    }

    public void getFilteredEntities(EntityFilter searchFilter, Array<Entity> result) {
        filterEntities(entities, searchFilter, result);
    }

    private void filterEntities(Array<Entity> entities, EntityFilter searchFilter, Array<Entity> result) {
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);
            if (searchFilter == null || searchFilter.match(entity)) {
                result.add(entity);
            }
        }
    }

    public Array<RegionNode> getRegions() {
        Array<RegionNode> regions = new Array();
        for (int x = 0; x < numCellsX; x++) {
            for (int y = 0; y < numCellsY; y++) {
                Cell cell = cells[x][y];
                RegionNode region = cell.region;
                if (!regions.contains(region, false)) {
                    regions.add(region);
                }
            }
        }
        return regions;
    }

    public void printRegions() {
        StringBuilder out = new StringBuilder();
        for (int y = 0; y < numCellsY; y++) {
            for (int x = 0; x < numCellsX; x++) {
                if (x != 0) {
                    out.append(",");
                }
                out.append(cells[x][y].region.regionId);
            }
            out.append("\n");
        }
        System.out.println(out.toString());
    }

    public int getCellsX() {
        return numCellsX;
    }

    public int getCellsY() {
        return numCellsY;
    }
}
