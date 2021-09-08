package com.emptypocketstudios.boardgame.engine.world.processors;

import com.badlogic.gdx.math.Rectangle;
import com.emptypocketstudios.boardgame.engine.world.CellTypes;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;

import junit.framework.TestCase;

import org.junit.Test;

public class WorldChunkRegionProcessorTest extends TestCase {

    WorldChunk chunk;
    WorldChunkRegionProcessor processor;

    public void setUp() throws Exception {
        super.setUp();
        chunk = new WorldChunk();
        chunk.init(null, new Rectangle(), 0, 0, 20, 20);
        processor = new WorldChunkRegionProcessor(chunk);
    }

    @Test
    public void ensureEmptyRegionFullyGetProcessed() {
        processor.run();
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                assertEquals(1, chunk.getCellByIndex(x, y).region.regionId);
            }
        }
    }

    @Test
    public void testBlockedRegionGetEmpty() {
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                chunk.cells[x][y].type = CellTypes.ROCK;
            }
        }
        processor.run();
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                assertEquals(WorldChunkRegionProcessor.RESET_LAYER_ID, chunk.getCellByIndex(x, y).region.regionId);
            }
        }
    }

    @Test
    public void testSubRegionTaggedCorrectly() {
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                if (x >= 5 && x <= 10 && (y == 5 || y == 10)) {
                    chunk.cells[x][y].type = CellTypes.ROCK;
                }
                if (y >= 5 && y <= 10 && (x == 5 || x == 10)) {
                    chunk.cells[x][y].type = CellTypes.ROCK;
                }
            }
        }
        processor.run();
        assertEquals(1, chunk.getCellByIndex(1, 1).region.regionId);
        assertEquals(2, chunk.getCellByIndex(6, 6).region.regionId);
    }
}