package com.emptypocketstudios.boardgame.engine.world.processors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;

public class WorldLoadingProcessor {

    World world;

    public WorldLoadingProcessor(World world) {
        this.world = world;
    }

    public void loadAllChunks() {
        for (int x = 0; x < world.chunksX; x++) {
            for (int y = 0; y < world.chunksY; y++) {
                loadChunk(x, y);
            }
        }
    }

    public void loadChunk(int x, int y) {
        if (x < 0 || y < 0 || x >= world.chunksX || y >= world.chunksY) {
            return;
        }
        Rectangle subRegion = Pools.obtain(Rectangle.class);
        subRegion.width = world.boundary.width / world.chunksX;
        subRegion.height = world.boundary.height / world.chunksY;
        subRegion.x = world.boundary.x + subRegion.width * x;
        subRegion.y = world.boundary.y + subRegion.height * y;

        WorldChunk chunk = Pools.obtain(WorldChunk.class);
        chunk.init(world, subRegion, x, y, world.cellsPerChunkX, world.cellsPerChunkY);
        chunk.setupInternalLinks();
        world.chunks[x][y] = chunk;
        linkChunk(x, y);

    }

    public void linkChunk(int chunkX, int chunkY) {
        WorldChunk currentChunk = world.getChunkByChunkId(chunkX, chunkY);
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
        currentCell.link(dx, dy, world.getCellByOffset(currentCell, dx, dy));

        // Link Up Right
        currentCell = currentChunk.getCellByIndex(currentChunk.numCellsX - 1, currentChunk.numCellsY - 1);
        dx = +1;
        dy = +1;
        currentCell.link(dx, dy, world.getCellByOffset(currentCell, dx, dy));

        //Link Down Right
        currentCell = currentChunk.getCellByIndex(currentChunk.numCellsX - 1, 0);
        dx = +1;
        dy = -1;
        currentCell.link(dx, dy, world.getCellByOffset(currentCell, dx, dy));

        // Link Up Left
        currentCell = currentChunk.getCellByIndex(0, currentChunk.numCellsY - 1);
        dx = -1;
        dy = +1;
        currentCell.link(dx, dy, world.getCellByOffset(currentCell, dx, dy));


        //Link Up
        chunk = world.getChunkByChunkId(chunkX, chunkY + 1);
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
        chunk = world.getChunkByChunkId(chunkX, chunkY - 1);
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
        chunk = world.getChunkByChunkId(chunkX - 1, chunkY);
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
        chunk = world.getChunkByChunkId(chunkX + 1, chunkY);
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
        if (x < 0 || y < 0 || x >= world.chunksX || y >= world.chunksY) {
            return false;
        }
        return world.getChunkByChunkId(x, y) == null;
    }

    public void unloadChunk(int x, int y) {
        if (x < 0 || y < 0 || x >= world.chunksX || y >= world.chunksY) {
            return;
        }
        WorldChunk chunk = world.chunks[x][y];
        world.chunks[x][y] = null;
        Pools.free(chunk);
    }

    public void fill(CellType cellType) {
        for (int x = 0; x < world.chunksX; x++) {
            for (int y = 0; y < world.chunksY; y++) {
                WorldChunk chunk = world.chunks[x][y];
                chunk.fillAllCells(cellType);
            }
        }
    }
}
