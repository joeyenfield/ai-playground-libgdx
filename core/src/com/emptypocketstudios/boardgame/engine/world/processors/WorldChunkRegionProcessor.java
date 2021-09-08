package com.emptypocketstudios.boardgame.engine.world.processors;

import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellTypes;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;

public class WorldChunkRegionProcessor extends WorldChunkProcessor {
    public static final int RESET_LAYER_ID = -1000;

    public WorldChunkRegionProcessor(WorldChunk chunk) {
        super(chunk);
    }

    @Override
    protected void run() {
        //Reset Layer Id's
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                chunk.cells[x][y].region.regionId = RESET_LAYER_ID;
            }
        }
        int layerId = 1;
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                if (expand(x, y, layerId) > 0) {
                    layerId++;
                }
            }
        }
    }

    private int expand(int x, int y, int newLayerId) {
        int updatedCount = 0;
        Cell c = chunk.getCellByIndex(x, y);
        if (c != null && c.region.regionId == RESET_LAYER_ID && !CellTypes.isBlocked(c.type)) {
            c.region.regionId = newLayerId;
            updatedCount++;
            updatedCount += expand(x - 1, y, newLayerId);
            updatedCount += expand(x + 1, y, newLayerId);
            updatedCount += expand(x, y - 1, newLayerId);
            updatedCount += expand(x, y + 1, newLayerId);

//            if (chunk.world.allowDiagnoals) {
//                updatedCount += expand(x - 1, y - 1, newLayerId);
//                updatedCount += expand(x + 1, y + 1, newLayerId);
//                updatedCount += expand(x - 1, y + 1, newLayerId);
//                updatedCount += expand(x + 1, y - 1, newLayerId);
//            }

        }
        return updatedCount;
    }
}
