package com.emptypocketstudios.boardgame.engine.world;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.world.processors.WorldChunkRegionLinkProcessor;
import com.emptypocketstudios.boardgame.engine.world.processors.WorldChunkRegionProcessor;
import com.emptypocketstudios.boardgame.engine.world.processors.WorldChunkCellLinkProcessor;

public class WorldChunk implements Pool.Poolable {
    public World world;
    public GridPoint2 chunkId = new GridPoint2();
    public Rectangle boundary = new Rectangle();

    public int numCellsX = 1;
    public int numCellsY = 1;
    public Cell[][] cells;

    public Array<Entity> entities = new Array<>();

    public boolean updateRegionsRequired = true;
    public boolean updateRegionLinksRequired = true;

    public WorldChunkRegionProcessor worldChunkRegionProcessor;
    public WorldChunkRegionLinkProcessor worldChunkRegionLinkProcessor;
    public WorldChunkCellLinkProcessor worldChunkCellLinkProcessor;

    public Array<WorldChunkRegionNodeLink> regionNodeLinks = new Array<>();
    public Array<WorldChunkRegionNodeLink> regionNodeDiagonalLinks = new Array<>();

    public WorldChunk() {
        this.worldChunkCellLinkProcessor = new WorldChunkCellLinkProcessor(this);
        this.worldChunkRegionProcessor = new WorldChunkRegionProcessor(this);
        this.worldChunkRegionLinkProcessor = new WorldChunkRegionLinkProcessor(this);
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
                cells[x][y].init(chunkId, cellId, subRegion, CellTypes.GRASS);
            }
        }

        updateRegionsRequired = true;
        updateRegionLinksRequired = true;

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
            this.updateRegionLinksRequired = true;
            this.updateRegionsRequired = false;
        }
    }

    public void updateRegionLinks() {
        if (updateRegionLinksRequired) {
            this.worldChunkRegionLinkProcessor.process();
            this.updateRegionLinksRequired = false;
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
                WorldChunk newChunk = world.getChunkByWorldPosition(e.pos);
                if (newChunk != null) {
                    removeEntity(e);
                    newChunk.addEntity(e);
                }
            }
        }
    }


    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void removeEntity(Entity e) {
        entities.removeValue(e, false);
    }


    public void fillAllCells(short type) {
        for (int x = 0; x < numCellsX; x++) {
            for (int y = 0; y < numCellsY; y++) {
                Cell c = cells[x][y];
                cells[x][y].type = type;
                updateRegionsRequired = true;
            }
        }
    }
}
