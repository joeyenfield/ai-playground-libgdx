package com.emptypocketstudios.boardgame.engine.pathfinding;

import com.emptypocketstudios.boardgame.TestUtil;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.EngineSetupConfig;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingResponse;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.engine.world.map.MapGeneratorType;

import junit.framework.TestCase;

public class PathFinderTest extends TestCase {
    Engine engine;

    @Override
    public void setUp() {
        engine = TestUtil.setupTestWorld();
    }

    public void testSameCellPath() {
        Cell start = engine.world.getCellByCellId(0, 0);
        Cell end = engine.world.getCellByCellId(0, 0);

        PathFindingResponse response = new PathFindingResponse();
        engine.pathFinderManager.pathFinder.getPathFindingResponse(start, end, response);
        assertEquals(PathFindingResultEnum.SUCCESS, response.regionSearchResult);
        assertEquals(PathFindingResultEnum.SUCCESS, response.cellSearchResult);
        assertEquals(1, response.path.size);
        assertEquals(0, response.path.get(0).cellId.x);
        assertEquals(0, response.path.get(0).cellId.y);
    }

    public void testRepeatedTests() {
        EngineSetupConfig setup = new EngineSetupConfig();
        setup.map = MapGeneratorType.BASIC;
        setup.entities = 0;
        setup.buildings = 0;
        setup.towns = 0;
        setup.cellsPerChunkX = 16;
        setup.cellsPerChunkY = 16;
        setup.chunksY = 16;
        setup.chunksX = 16;
        Engine engine = TestUtil.setupTestWorld(setup);
        World world = engine.world;

//        for (int i = 0; i < 1000; i++) {
        int i = 0;
        while (true) {
            i++;
            int y = i % (world.getCellsY());
            System.out.println("Run : " + (i + 1));
            Cell start = world.getCellByCellId(0, y);
            Cell end = world.getCellByCellId(world.getCellsX() - 1, world.getCellsY() - y - 1);
            PathFindingResponse response = new PathFindingResponse();
            engine.pathFinderManager.pathFinder.getPathFindingResponse(start, end, response);
            assertEquals(PathFindingResultEnum.SUCCESS, response.cellSearchResult);
        }

    }

    public void testCrossChunkWithRoadsTest() {
        //Set Stripes
        World world = engine.world;
        for (int x = 0; x < world.getCellsX(); x++) {
            for (int y = 0; y < world.getCellsY(); y++) {
                world.getCellByCellId(x, y).setRoad(x % 2 == 0);
            }
        }
        engine.update(1);

        Cell start = engine.world.getCellByCellId(0, 0);
        Cell end = engine.world.getCellByCellId(world.getCellsX() - 1, 0);

        PathFindingResponse response = new PathFindingResponse();
        engine.pathFinderManager.pathFinder.getPathFindingResponse(start, end, response);
        assertEquals(PathFindingResultEnum.SUCCESS, response.regionSearchResult);
        assertEquals(PathFindingResultEnum.SUCCESS, response.cellSearchResult);
        assertEquals(world.getCellsX(), response.path.size);
        assertEquals(0, response.path.get(0).cellId.x);
        assertEquals(0, response.path.get(0).cellId.y);

        assertEquals(world.getCellsX() - 1, response.path.get(world.getCellsX() - 1).cellId.x);
        assertEquals(0, response.path.get(0).cellId.y);
    }

    public void testCrossChunkWithTypeStripesTest() {
        //Set Stripes
        engine.update(1);
        World world = engine.world;
        for (int x = 0; x < world.getCellsX(); x++) {
            for (int y = 0; y < world.getCellsY(); y++) {
                int types = 10;
                int idx = (x % types) % 2;

                CellType type;
                boolean isRoad;
                int variant;

                switch (idx) {
                    case 0:
                        type = CellType.GRASS;
                        isRoad = false;
                        variant = 0;
                        break;
                    case 1:
                        type = CellType.GRASS;
                        isRoad = true;
                        variant = 0;
                        break;
                    case 2:
                        type = CellType.FOREST;
                        isRoad = false;
                        variant = CellType.FOREST.variants - 1;
                        break;
                    case 3:
                        type = CellType.SAND;
                        isRoad = false;
                        variant = 0;
                        break;
                    case 4:
                        type = CellType.SHALLOW_WATER;
                        isRoad = false;
                        variant = 0;
                        break;
                    default:
                        type = CellType.GRASS;
                        isRoad = false;
                        variant = 0;
                }

                world.getCellByCellId(x, y).setType(type, variant, isRoad);
            }
        }

        engine.update(1);

        Cell start = engine.world.getCellByCellId(0, 0);
        Cell end = engine.world.getCellByCellId(world.getCellsX() - 1, 0);

        PathFindingResponse response = new PathFindingResponse();
        engine.pathFinderManager.pathFinder.getPathFindingResponse(start, end, response);
        assertEquals(PathFindingResultEnum.SUCCESS, response.regionSearchResult);
        assertEquals(PathFindingResultEnum.SUCCESS, response.cellSearchResult);
        assertEquals(world.getCellsX(), response.path.size);
        assertEquals(0, response.path.get(0).cellId.x);
        assertEquals(0, response.path.get(0).cellId.y);

        assertEquals(world.getCellsX() - 1, response.path.get(world.getCellsX() - 1).cellId.x);
        assertEquals(0, response.path.get(0).cellId.y);
    }


}