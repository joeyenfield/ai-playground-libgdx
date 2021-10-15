package com.emptypocketstudios.boardgame.engine.pathfinding.layers;

import com.badlogic.gdx.utils.Array;
import com.emptypocketstudios.boardgame.TestUtil;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.EngineSetupConfig;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFindingResultEnum;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;
import com.emptypocketstudios.boardgame.engine.world.map.MapGeneratorType;

import junit.framework.TestCase;

public class RegionNodePathFinderNGTest extends TestCase {

    RegionNodePathFinderNG pathFinder;
    Engine engine;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        EngineSetupConfig config = new EngineSetupConfig();
        config.chunksX = 3;
        config.chunksY = 3;
        config.cellsPerChunkX = 5;
        config.cellsPerChunkY = 5;
        config.entities = 0;
        config.buildings = 0;
        config.towns = 0;
        config.map = MapGeneratorType.FLAT_LAND;

        pathFinder = new RegionNodePathFinderNG();
        engine = TestUtil.setupTestWorld(config);
        engine.update(1);
        World world = engine.world;
        for (int cX = 0; cX < world.getChunksX(); cX++) {
            for (int cY = 0; cY < world.getChunksY(); cY++) {
                WorldChunk chunk = world.getChunkByChunkId(cX, cY);
                for (int x = 0; x < chunk.getCellsX(); x++) {
                    for (int y = 0; y < chunk.getCellsY(); y++) {
                        Cell cell = chunk.getCellByIndex(x, y);
                        if (cell == null) {
                            System.out.println("Chunk[" + cX + "," + cY + "] - Cell[" + x + "," + y + "]");
                        }
                        if (x == 2 | y == 2) {
                            cell.setType(CellType.SAND);
                        }
                    }
                }
            }
        }
        engine.update(1);
    }

    public void testSetup() {
        World world = engine.world;
        assertEquals(5, world.getChunkByChunkId(0, 0).getRegions().size);
        assertEquals(5, world.getChunkByChunkId(1, 0).getRegions().size);
        assertEquals(5, world.getChunkByChunkId(2, 0).getRegions().size);
        assertEquals(5, world.getChunkByChunkId(0, 1).getRegions().size);
        assertEquals(5, world.getChunkByChunkId(1, 1).getRegions().size);
        assertEquals(5, world.getChunkByChunkId(2, 1).getRegions().size);
        assertEquals(5, world.getChunkByChunkId(0, 2).getRegions().size);
        assertEquals(5, world.getChunkByChunkId(1, 2).getRegions().size);
        assertEquals(5, world.getChunkByChunkId(2, 2).getRegions().size);

        assertEquals(45, world.regionLinks.size());
        assertEquals(45, world.diagonalRegionLinks.size());
        world.printRegions();
        world.printCellType();
    }

    public void testRegionInSameWithoutDiagonals() {
        World world = engine.world;
        world.fillAllCells(CellType.GRASS);
        world.update(1);

        assertEquals(9, world.regionLinks.size());
        assertEquals(9, world.diagonalRegionLinks.size());

        Cell startCell = world.getChunkByChunkId(0, 0).getCellByCellId(0, 0);
        Cell endCell = world.getChunkByChunkId(0, 0).getCellByCellId(0, 0);

        Array<RegionNode> regions = new Array<>();
        pathFinder.setup(world, startCell, endCell, regions, 9999, false);
        pathFinder.processAll();
        assertEquals(1, regions.size);
    }

    public void testRegionInSameWithDiagonals() {
        World world = engine.world;
        world.fillAllCells(CellType.GRASS);
        world.update(1);

        assertEquals(9, world.regionLinks.size());
        assertEquals(9, world.diagonalRegionLinks.size());

        Cell startCell = world.getChunkByChunkId(0, 0).getCellByCellId(0, 0);
        Cell endCell = world.getChunkByChunkId(0, 0).getCellByCellId(0, 0);

        Array<RegionNode> regions = new Array<>();
        pathFinder.setup(world, startCell, endCell, regions, 9999, true);
        pathFinder.processAll();
        assertEquals(1, regions.size);
    }

    public void testCrossRegionWithoutDiagonals() {
        World world = engine.world;
        world.fillAllCells(CellType.GRASS);
        world.update(1);

        assertEquals(9, world.regionLinks.size());
        assertEquals(9, world.diagonalRegionLinks.size());

        Cell cell;
        Array<RegionNode> nodes;
        Array<RegionNode> nodesDiagonal;

        cell = world.getChunkByChunkId(0, 0).getCellByIndex(0, 0);
        nodes = world.regionLinks.get(cell.region);
        nodesDiagonal = world.diagonalRegionLinks.get(cell.region);
        assertEquals(3, nodes.size);
        assertEquals(4, nodesDiagonal.size);

        cell = world.getChunkByChunkId(1, 1).getCellByIndex(0, 0);
        nodes = world.regionLinks.get(cell.region);
        nodesDiagonal = world.diagonalRegionLinks.get(cell.region);
        assertEquals(5, nodes.size);
        assertEquals(9, nodesDiagonal.size);


        Array<RegionNode> regions;
        Cell startCell;
        Cell endCell;

        //Region DOWN
        startCell = world.getChunkByChunkId(0, 1).getCellByIndex(0, 0);
        endCell = world.getChunkByChunkId(0, 0).getCellByIndex(0, 0);
        regions = new Array<>();
        pathFinder.setup(world, startCell, endCell, regions, 9999, false);
        assertEquals(PathFindingResultEnum.SUCCESS, pathFinder.processAll());
        assertEquals(2, regions.size);

        //Region UP
        startCell = world.getChunkByChunkId(0, 0).getCellByIndex(0, 0);
        endCell = world.getChunkByChunkId(0, 1).getCellByIndex(0, 0);
        regions = new Array<>();
        pathFinder.setup(world, startCell, endCell, regions, 9999, false);
        assertEquals(PathFindingResultEnum.SUCCESS, pathFinder.processAll());
        assertEquals(2, regions.size);

        //Region Right
        startCell = world.getChunkByChunkId(0, 0).getCellByIndex(0, 0);
        endCell = world.getChunkByChunkId(1, 0).getCellByIndex(0, 0);
        regions = new Array<>();
        pathFinder.setup(world, startCell, endCell, regions, 9999, false);
        assertEquals(PathFindingResultEnum.SUCCESS,
                pathFinder.processAll()
        );
        assertEquals(2, regions.size);

        //Region Left
        startCell = world.getChunkByChunkId(1, 0).getCellByIndex(0, 0);
        endCell = world.getChunkByChunkId(0, 0).getCellByIndex(0, 0);
        regions = new Array<>();
        pathFinder.setup(world, startCell, endCell, regions, 9999, false);
        assertEquals(PathFindingResultEnum.SUCCESS, pathFinder.processAll());
        assertEquals(2, regions.size);


    }
}