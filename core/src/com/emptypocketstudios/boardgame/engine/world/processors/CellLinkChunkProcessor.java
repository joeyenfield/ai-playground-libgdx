package com.emptypocketstudios.boardgame.engine.world.processors;

import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;

public class CellLinkChunkProcessor extends WorldChunkProcessor {

    public CellLinkChunkProcessor(WorldChunk chunk) {
        super(chunk);
    }

    @Override
    protected void run() {
        int dx = 0;
        int dy = 0;
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                Cell cell = chunk.getCellByIndex(x, y);

                for (dx = -1; dx <= 1; dx++) {
                    for (dy = -1; dy <= 1; dy++) {
                        if (!(dx == 0 && dy == 0)) {
                            cell.link(dx, dy, chunk.getCellByIndex(x + dx, y + dy));
                        }
                    }
                }
            }
        }


    }
}
