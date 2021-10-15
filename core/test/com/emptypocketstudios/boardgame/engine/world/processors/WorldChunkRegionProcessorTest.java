package com.emptypocketstudios.boardgame.engine.world.processors;

import com.badlogic.gdx.utils.Array;
import com.emptypocketstudios.boardgame.TestUtil;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.EngineSetupConfig;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;
import com.emptypocketstudios.boardgame.engine.world.map.MapGeneratorType;

import junit.framework.TestCase;

import java.util.Map;

public class WorldChunkRegionProcessorTest extends TestCase {
    int numCellsX = 20;
    int numCellsY = 20;
    Engine engine;
    WorldChunk chunk;
    WorldChunkRegionProcessor processor;

    public void setUp() throws Exception {
        super.setUp();
        EngineSetupConfig config = new EngineSetupConfig();
        config.entities = 0;
        config.towns = 0;
        config.buildings = 0;
        config.chunksX = 3;
        config.chunksY = 3;
        config.cellsPerChunkX = numCellsX;
        config.cellsPerChunkY = numCellsY;
        config.map = MapGeneratorType.CROSS_ROAD;
        engine = TestUtil.setupTestWorld(config);
        engine.update(1);

        chunk = engine.world.getChunkByChunkId(1, 1);
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

    public void testSetupCorrectRegions() {
        EngineSetupConfig config = new EngineSetupConfig();
        config.entities = 0;
        config.towns = 0;
        config.buildings = 0;
        config.chunksX = 3;
        config.chunksY = 3;
        config.cellsPerChunkX = 3;
        config.cellsPerChunkY = 3;
        config.debug=true;
        config.map = MapGeneratorType.FLAT_LAND;
        engine = TestUtil.setupTestWorld(config);
        engine.update(1);
        World world = engine.world;
        //Ensure correct number of regions
        engine.world.fillAllCells(CellType.GRASS);
        world.update(1);

        world.printRegionLinks();

        assertEquals(9, world.regionLinks.size());
        assertEquals(9, world.diagonalRegionLinks.size());

        RegionNode BL = world.getChunkByChunkId(0, 0).getCellByIndex(0, 0).region;
        RegionNode BM = world.getChunkByChunkId(1, 0).getCellByIndex(0, 0).region;
        RegionNode BR = world.getChunkByChunkId(2, 0).getCellByIndex(0, 0).region;

        RegionNode ML = world.getChunkByChunkId(0, 1).getCellByIndex(0, 0).region;
        RegionNode MM = world.getChunkByChunkId(1, 1).getCellByIndex(0, 0).region;
        RegionNode MR = world.getChunkByChunkId(2, 1).getCellByIndex(0, 0).region;

        RegionNode TL = world.getChunkByChunkId(0, 2).getCellByIndex(0, 0).region;
        RegionNode TM = world.getChunkByChunkId(1, 2).getCellByIndex(0, 0).region;
        RegionNode TR = world.getChunkByChunkId(2, 2).getCellByIndex(0, 0).region;

        assertEquals(3, world.regionLinks.get(BL).size);
        assertEquals(4, world.regionLinks.get(BM).size);
        assertEquals(3, world.regionLinks.get(BR).size);
        assertEquals(4, world.regionLinks.get(ML).size);
        assertEquals(5, world.regionLinks.get(MM).size);
        assertEquals(4, world.regionLinks.get(MR).size);
        assertEquals(3, world.regionLinks.get(TL).size);
        assertEquals(4, world.regionLinks.get(TM).size);
        assertEquals(3, world.regionLinks.get(TR).size);

        assertContents(world.regionLinks.get(BL), ML, BM, BL);
        assertContents(world.regionLinks.get(BM), BL, MM, BR, BM);
        assertContents(world.regionLinks.get(BR), BM, MR, BR);
        assertContents(world.regionLinks.get(ML), BL, MM, TL, ML);
        assertContents(world.regionLinks.get(MM), BM, ML, MR, TM, MM);
        assertContents(world.regionLinks.get(MR), TR, BR, MM, MR);
        assertContents(world.regionLinks.get(TL), ML, TM, TL);
        assertContents(world.regionLinks.get(TM), TR, MM, TL, TM);
        assertContents(world.regionLinks.get(TR), TM, MR, TR);

        assertEquals(4, world.diagonalRegionLinks.get(BL).size);
        assertEquals(6, world.diagonalRegionLinks.get(BM).size);
        assertEquals(4, world.diagonalRegionLinks.get(BR).size);
        assertEquals(6, world.diagonalRegionLinks.get(ML).size);
        assertEquals(9, world.diagonalRegionLinks.get(MM).size);
        assertEquals(6, world.diagonalRegionLinks.get(MR).size);
        assertEquals(4, world.diagonalRegionLinks.get(TL).size);
        assertEquals(6, world.diagonalRegionLinks.get(TM).size);
        assertEquals(4, world.diagonalRegionLinks.get(TR).size);

        //Clockwise
        assertContents(world.diagonalRegionLinks.get(BL), ML, MM, BM, BL);
        assertContents(world.diagonalRegionLinks.get(BM), BL, ML, MM, MR, BR, BM);
        assertContents(world.diagonalRegionLinks.get(BR), BM, MM, MR, BR);
        assertContents(world.diagonalRegionLinks.get(ML), TL, TM, MM, BM, BL, ML);
        assertContents(world.diagonalRegionLinks.get(MM), ML, TL, TM, TR, MR, BR, BM, BL, MM);
        assertContents(world.diagonalRegionLinks.get(MR), MM, TM, TR, BR, BM, MR);
        assertContents(world.diagonalRegionLinks.get(TL), TM, MM, ML, TL);
        assertContents(world.diagonalRegionLinks.get(TM), TL, TR, ML, MM, MR, TM);
        assertContents(world.diagonalRegionLinks.get(TR), TM, MR, MM, TR);
    }

    public void assertContents(Array<RegionNode> regionNodes, RegionNode... nodes) {
        Array<RegionNode> nodeTest = new Array<>();
        nodeTest.addAll(regionNodes);

        for (RegionNode node : nodes) {
            assertTrue("Testing Node [" + node + "] - not in [" + regionNodes.toString(",") + "]"
                    , nodeTest.contains(node, false));
            nodeTest.removeValue(node, false);
        }
        assertEquals(0, nodeTest.size);
    }

    public void testLinksSubRegions() {
        EngineSetupConfig config = new EngineSetupConfig();
        config.entities = 0;
        config.towns = 0;
        config.buildings = 0;
        config.chunksX = 3;
        config.chunksY = 3;
        config.cellsPerChunkX = 5;
        config.cellsPerChunkY = 5;
        config.map = MapGeneratorType.FLAT_LAND;
        engine = TestUtil.setupTestWorld(config);
        engine.update(1);
        World world = engine.world;
        //Ensure correct number of regions
        engine.world.fillAllCells(CellType.GRASS);
        world.update(1);
        assertEquals(9, world.regionLinks.size());
        assertEquals(9, world.diagonalRegionLinks.size());

        WorldChunk chunk = world.getChunkByChunkId(1, 1);

        int boxInset = 1;
        for (int x = 0; x < chunk.getCellsX(); x++) {
            for (int y = 0; y < chunk.getCellsY(); y++) {
                Cell cell = chunk.getCellByIndex(x, y);
                if (x >= boxInset && y >= boxInset && x < chunk.getCellsX() - boxInset && y < chunk.getCellsY() - boxInset) {
                    cell.setType(CellType.SAND);
                }
            }
        }
        world.update(1);

        assertEquals(10, world.regionLinks.size());
        assertEquals(10, world.diagonalRegionLinks.size());
        assertEquals(2, world.regionLinks.get(chunk.getCellByIndex(2, 2).region).size);
    }
}