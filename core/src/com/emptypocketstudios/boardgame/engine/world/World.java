package com.emptypocketstudios.boardgame.engine.world;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class World {
    public Engine engine;
    public int chunksX;
    public int chunksY;
    public int cellsPerChunkX;
    public int cellsPerChunkY;
    public Rectangle boundary = new Rectangle();
    public WorldChunk[][] chunks;
    public Map<String, Entity> namedEntities = new HashMap<>();

    public World(Rectangle boundary, int chunksX, int chunksY, int cellsPerChunkX, int cellsPerChunkY) {
        this.chunksX = chunksX;
        this.chunksY = chunksY;
        this.cellsPerChunkX = cellsPerChunkX;
        this.cellsPerChunkY = cellsPerChunkY;
        this.boundary.set(boundary);
        this.chunks = new WorldChunk[chunksX][chunksY];
    }

    public void loadAllChunks() {
        for (int x = 0; x < chunksX; x++) {
            for (int y = 0; y < chunksY; y++) {
                loadChunk(x, y);
            }
        }
    }

    public void addEntity(Entity e) {
        e.world = this;
        namedEntities.put(e.name, e);
        engine.postOffice.register(e.name, e);
        WorldChunk chunk = getChunkByWorldPosition(e.pos);
        if (chunk != null) {
            chunk.addEntity(e);
        }
    }


    public WorldChunk getChunkByWorldPosition(Vector2 pos) {
        if (boundary.contains(pos)) {
            int x = (int) (((pos.x - boundary.x) / boundary.width) * chunksX);
            int y = (int) (((pos.y - boundary.y) / boundary.height) * chunksY);
            return getChunkByChunkId(x, y);
        }
        return null;
    }

    public Cell getCellAtWorldPosition(Vector2 pos) {
        if (boundary.contains(pos)) {
            int x = (int) (((pos.x - boundary.x) / boundary.width) * chunksX * cellsPerChunkX);
            int y = (int) (((pos.y - boundary.y) / boundary.height) * chunksY * cellsPerChunkY);
            return getCellByCellId(x, y);
        }
        return null;
    }

    public Cell getCellByCellId(int x, int y) {
        //Work out chunks
        int chunkX = x / cellsPerChunkX;
        int chunkY = y / cellsPerChunkY;
        WorldChunk chunk = getChunkByChunkId(chunkX, chunkY);
        if (chunk != null) {
            return chunk.getCellByCellId(x, y);
        }
        return null;
    }

    public Cell getCellByOffset(Cell target, int x, int y) {
        return getCellByCellId(target.cellId.x + x, target.cellId.y + y);
    }

    public WorldChunk getChunkByChunkId(int x, int y) {
        if (x < 0 || y < 0 || x >= chunksX || y >= chunksY) {
            return null;
        }
        return chunks[x][y];
    }

    public void loadChunk(int x, int y) {
        if (x < 0 || y < 0 || x >= chunksX || y >= chunksY) {
            return;
        }
        Rectangle subRegion = Pools.obtain(Rectangle.class);
        subRegion.width = boundary.width / chunksX;
        subRegion.height = boundary.height / chunksY;
        subRegion.x = boundary.x + subRegion.width * x;
        subRegion.y = boundary.y + subRegion.height * y;

        WorldChunk chunk = Pools.obtain(WorldChunk.class);
        chunk.init(this, subRegion, x, y, cellsPerChunkX, cellsPerChunkY);
        chunk.setupInternalLinks();
        chunks[x][y] = chunk;
        linkChunk(x, y);

    }

    public void linkChunk(int chunkX, int chunkY) {
        WorldChunk currentChunk = getChunkByChunkId(chunkX, chunkY);
        if (currentChunk == null) {
            return;
        }

        WorldChunk chunk;
        Cell currentCell = null;
        int dx = 0;
        int dy = 0;

        //Link Down Left
        currentCell = currentChunk.getCellByIndex(0, 0);
        dx = -1;
        dy = -1;
        currentCell.link(dx, dy, getCellByOffset(currentCell, dx, dy));

        // Link Up Right
        currentCell = currentChunk.getCellByIndex(currentChunk.numCellsX - 1, currentChunk.numCellsY - 1);
        dx = +1;
        dy = +1;
        currentCell.link(dx, dy, getCellByOffset(currentCell, dx, dy));

        //Link Down Right
        currentCell = currentChunk.getCellByIndex(currentChunk.numCellsX - 1, 0);
        dx = +1;
        dy = -1;
        currentCell.link(dx, dy, getCellByOffset(currentCell, dx, dy));

        // Link Up Left
        currentCell = currentChunk.getCellByIndex(0, currentChunk.numCellsY - 1);
        dx = -1;
        dy = +1;
        currentCell.link(dx, dy, getCellByOffset(currentCell, dx, dy));


        //Link Up
        chunk = getChunkByChunkId(chunkX, chunkY + 1);
        if (chunk != null) {
            for (int x = 0; x < currentChunk.numCellsX; x++) {
                currentCell = currentChunk.getCellByIndex(x, currentChunk.numCellsY - 1);

                dy = +1;
                dx = -1;
                currentCell.link(dx, dy, chunk.getCellByCellId(currentCell.cellId.x + dx, currentCell.cellId.y + dy));
                dx = 0;
                currentCell.link(dx, dy, chunk.getCellByCellId(currentCell.cellId.x + dx, currentCell.cellId.y + dy));
                dx = 1;
                currentCell.link(dx, dy, chunk.getCellByCellId(currentCell.cellId.x + dx, currentCell.cellId.y + dy));
            }
        }

        //Link Down
        chunk = getChunkByChunkId(chunkX, chunkY - 1);
        if (chunk != null) {
            for (int x = 0; x < currentChunk.numCellsX; x++) {
                currentCell = currentChunk.getCellByIndex(x, 0);
                dy = -1;
                dx = -1;
                currentCell.link(dx, dy, chunk.getCellByCellId(currentCell.cellId.x + dx, currentCell.cellId.y + dy));
                dx = 0;
                currentCell.link(dx, dy, chunk.getCellByCellId(currentCell.cellId.x + dx, currentCell.cellId.y + dy));
                dx = 1;
                currentCell.link(dx, dy, chunk.getCellByCellId(currentCell.cellId.x + dx, currentCell.cellId.y + dy));
            }
        }

        //Link Left
        chunk = getChunkByChunkId(chunkX - 1, chunkY);
        if (chunk != null) {
            for (int y = 0; y < currentChunk.numCellsY; y++) {
                currentCell = currentChunk.getCellByIndex(0, y);

                dx = -1;
                dy = -1;
                currentCell.link(dx, dy, chunk.getCellByCellId(currentCell.cellId.x + dx, currentCell.cellId.y + dy));
                dy = 0;
                currentCell.link(dx, dy, chunk.getCellByCellId(currentCell.cellId.x + dx, currentCell.cellId.y + dy));
                dy = 1;
                currentCell.link(dx, dy, chunk.getCellByCellId(currentCell.cellId.x + dx, currentCell.cellId.y + dy));
            }
        }

        //Link Right
        chunk = getChunkByChunkId(chunkX + 1, chunkY);
        if (chunk != null) {
            for (int y = 0; y < currentChunk.numCellsY; y++) {
                currentCell = currentChunk.getCellByIndex(currentChunk.numCellsX - 1, y);

                dx = 1;
                dy = -1;
                currentCell.link(dx, dy, chunk.getCellByCellId(currentCell.cellId.x + dx, currentCell.cellId.y + dy));
                dy = 0;
                currentCell.link(dx, dy, chunk.getCellByCellId(currentCell.cellId.x + dx, currentCell.cellId.y + dy));
                dy = 1;
                currentCell.link(dx, dy, chunk.getCellByCellId(currentCell.cellId.x + dx, currentCell.cellId.y + dy));
            }
        }
    }

    public boolean isChunkLoaded(int x, int y) {
        if (x < 0 || y < 0 || x >= chunksX || y >= chunksY) {
            return false;
        }
        return getChunkByChunkId(x, y) == null;
    }

    public void unloadChunk(int x, int y) {
        if (x < 0 || y < 0 || x >= chunksX || y >= chunksY) {
            return;
        }
        WorldChunk chunk = chunks[x][y];
        chunks[x][y] = null;
        Pools.free(chunk);
    }

    public WorldChunk getChunkByChunkId(GridPoint2 chunkId) {
        return getChunkByChunkId(chunkId.x, chunkId.y);
    }

    public void update(float delta) {
        for (int x = 0; x < chunksX; x++) {
            for (int y = 0; y < chunksY; y++) {
                WorldChunk chunk = chunks[x][y];
                if (chunk != null) {
                    chunk.update(delta);
                }
            }
        }
    }

    public Entity getEntityByName(String entityName) {
        return namedEntities.get(entityName);
    }

    public Entity getEntityByPos(Vector2 pos) {
        Entity bestEntity = null;
        float bestDistance = 0;
        for (Entity e : namedEntities.values()) {
            if (bestEntity == null) {
                bestEntity = e;
                bestDistance = bestEntity.pos.dst2(pos);
            } else {
                if (bestDistance > e.pos.dst2(pos)) {
                    bestDistance = e.pos.dst2(pos);
                    bestEntity = e;
                }
            }
        }
        return bestEntity;
    }
}
