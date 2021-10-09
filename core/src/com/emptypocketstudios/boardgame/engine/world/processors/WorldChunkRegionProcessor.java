package com.emptypocketstudios.boardgame.engine.world.processors;

import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;
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
                if (expand(x, y, null, layerId) > 0) {
                    layerId++;
                }
            }
        }
    }

    private boolean isSameRegion(Cell currentCell, Cell parent) {
        if (parent == null) {
            return true;
        }
        return parent.type == currentCell.type && parent.isRoad == currentCell.isRoad && parent.typeVariant == currentCell.typeVariant;
    }

    private int expand(int x, int y, Cell parent, int newLayerId) {
        int updatedCount = 0;
        Cell c = chunk.getCellByIndex(x, y);
        if (c != null && c.region.regionId == RESET_LAYER_ID) {
            if (isSameRegion(c, parent)) {
                c.region.regionId = newLayerId;
                c.region.regionWalkWeight = CellType.getTravelEffort(c);
                updatedCount++;
                updatedCount += expand(x - 1, y, c, newLayerId);
                updatedCount += expand(x + 1, y, c, newLayerId);
                updatedCount += expand(x, y - 1, c, newLayerId);
                updatedCount += expand(x, y + 1, c, newLayerId);

//            if (chunk.world.allowDiagnoals) {
//                updatedCount += expand(x - 1, y - 1, newLayerId);
//                updatedCount += expand(x + 1, y + 1, newLayerId);
//                updatedCount += expand(x - 1, y + 1, newLayerId);
//                updatedCount += expand(x + 1, y - 1, newLayerId);
//            }
            }
        }
        return updatedCount;
    }
}
