package com.emptypocketstudios.boardgame.engine.world.processors;

import com.badlogic.gdx.math.Rectangle;
import com.emptypocketstudios.boardgame.engine.world.CellType;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;

import junit.framework.TestCase;

public class WorldChunkRegionProcessorTest extends TestCase {
    int numCellsX = 20;
    int numCellsY = 20;
    WorldChunk chunk;
    WorldChunkRegionProcessor processor;

    public void setUp() throws Exception {
        super.setUp();
        chunk = new WorldChunk();
        chunk.init(null, new Rectangle(), 0, 0, numCellsX, numCellsY);
        processor = new WorldChunkRegionProcessor(chunk);
    }

    public void testEnsureEmptyRegionFullyGetProcessed() {
        processor.run();
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                assertEquals(1, chunk.getCellByIndex(x, y).region.regionId);
            }
        }
    }

    public void testEnsureVariantsGetDifferentRegionMapped() {
        chunk.fillAllCells(CellType.GRASS);
        processor.run();
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                assertEquals(1, chunk.getCellByIndex(x, y).region.regionId);
            }
        }

        chunk.fillAllCells(CellType.GRASS);
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                chunk.getCellByIndex(x, y).setType(CellType.GRASS, y);
            }
        }
        processor.run();
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                assertEquals(y + 1, chunk.getCellByIndex(x, y).region.regionId);
            }
        }


    }

    public void testEnsureRoadGetDifferentRegionMapped() {
        chunk.fillAllCells(CellType.GRASS);
        processor.run();
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                assertEquals(1, chunk.getCellByIndex(x, y).region.regionId);
            }
        }

        chunk.fillAllCells(CellType.GRASS);
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                chunk.getCellByIndex(x, y).setType(CellType.GRASS, 0, y % 2 == 0);
            }
        }
        processor.run();
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                assertEquals(y + 1, chunk.getCellByIndex(x, y).region.regionId);
            }
        }


    }

    public void testBlockedRegionGetVeryHighWeight() {
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                chunk.cells[x][y].type = CellType.WATER;
            }
        }
        processor.run();
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                assertTrue(100000 < chunk.getCellByIndex(x, y).region.regionWalkWeight);
            }
        }
    }

    public void testSubRegionTaggedCorrectly() {
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                if (x >= 5 && x <= 10 && (y == 5 || y == 10)) {
                    chunk.cells[x][y].type = CellType.ROCK;
                }
                if (y >= 5 && y <= 10 && (x == 5 || x == 10)) {
                    chunk.cells[x][y].type = CellType.ROCK;
                }
            }
        }
        processor.run();
        assertEquals(1, chunk.getCellByIndex(1, 1).region.regionId);
        assertEquals(3, chunk.getCellByIndex(6, 6).region.regionId);
    }


    public void testSubRegionTaggedCrossCorrectly() {

        chunk = new WorldChunk();
        chunk.init(null, new Rectangle(), 0, 0, 5, 5);
        processor = new WorldChunkRegionProcessor(chunk);

        chunk.fillAllCells(CellType.GRASS);
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                if (x == 2 || y == 2) {
                    chunk.cells[x][y].setType(CellType.SAND);
                }
            }
        }
        processor.run();

        chunk.printRegions();

        assertEquals(5, chunk.getRegions().size);
        assertEquals(1, chunk.getCellByIndex(0, 0).region.regionId);
        assertEquals(2, chunk.getCellByIndex(2, 2).region.regionId);
        assertEquals(4, chunk.getCellByIndex(4, 0).region.regionId);
        assertEquals(3, chunk.getCellByIndex(0, 4).region.regionId);
        assertEquals(5, chunk.getCellByIndex(4, 4).region.regionId);
    }
}