package com.emptypocketstudios.boardgame.engine.world.map;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.EngineSetupConfig;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.building.BuildingType;
import com.emptypocketstudios.boardgame.engine.entity.components.human.HumanType;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFinder;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingResponse;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.library.noise.NoiseGenerator;
import com.emptypocketstudios.boardgame.library.noise.NoiseLayer;
import com.emptypocketstudios.boardgame.library.noise.OpenSimplexNoise;

public class BasicMapGenerator extends MapGenerator {


    protected void generateMap(Engine engine) {
        engine.log("MapGenerator.generateMap", "Generating Map");
        //Clear world
        engine.world.fillAllCells(CellType.GRASS);

        float threshold = 0.4f;
        NoiseLayer layer = null;
        //Create Forests
        NoiseGenerator forest = new NoiseGenerator();

        layer = forest.addLayer();
        layer.octavesX = 0.5f;
        layer.octavesY = 0.5f;
        layer.setThreshold(0.65f);

        layer = forest.addLayer();
        layer.octavesX = engine.config.cellsPerChunkX / 3f;
        layer.octavesY = engine.config.cellsPerChunkY / 3f;
//        layer.setThreshold(0.7f);

//        forest.setMaxThreshold(0.47f);

        for (int x = 0; x < engine.config.cellsPerChunkX * engine.config.chunksX; x++) {
            for (int y = 0; y < engine.config.cellsPerChunkY * engine.config.chunksY; y++) {
                float noise = forest.getValue(x / (float) engine.config.cellsPerChunkX, y / (float) engine.config.cellsPerChunkY);

                boolean solid = noise > 0.47;
                int variant = (int) MathUtils.clamp(MathUtils.map(0.47f, 0.7f, 0, CellType.FOREST.variants - 1, noise), 0, CellType.FOREST.variants - 1);
                if (solid) {
                    Cell c = engine.world.getCellByCellId(x, y);
                    if (c != null && !c.isRoad) {
                        c.setType(CellType.FOREST, variant);
                    }
                }
            }
        }
        expand(engine, CellType.GRASS, CellType.FOREST, 2);
        expand(engine, CellType.GRASS, CellType.FOREST, 1);
        expand(engine, CellType.GRASS, CellType.FOREST, 0);

        OpenSimplexNoise simpNoise = new OpenSimplexNoise();
        //Create Water
        threshold = 0.9f;
        for (int x = 0; x < engine.config.cellsPerChunkX * engine.config.chunksX; x++) {
            for (int y = 0; y < engine.config.cellsPerChunkY * engine.config.chunksY; y++) {
                boolean solid = (simpNoise.eval(x / 2f + 55.14, y / 2f + 55.14) + simpNoise.eval(x / 20f + .5514, y / 20f + 15) + simpNoise.eval(x / 10f + 44, y / 10f + 55)) / 2 > threshold;
                if (solid) {
                    Cell c = engine.world.getCellByCellId(x, y);
                    if (c != null && !c.isRoad) {
                        c.setType(CellType.WATER);
                    }
                }
            }
        }

        //Create Rocks
        threshold = 0.8f;
        for (int x = 0; x < engine.config.cellsPerChunkX * engine.config.chunksX; x++) {
            for (int y = 0; y < engine.config.cellsPerChunkY * engine.config.chunksY; y++) {
                boolean solid = (simpNoise.eval(x / 2f + 3.14, y / 2f + 3.14) + simpNoise.eval(x / 20f + 3.14, y / 20f + 3.14) + simpNoise.eval(x / 10f + 3.14, y / 10f + 3.14)) / 2 > threshold;
                if (solid) {
                    Cell c = engine.world.getCellByCellId(x, y);
                    if (c != null && !c.isRoad) {
                        c.setType(CellType.ROCK);
                    }
                }
            }
        }
        engine.world.getCellByCellId(0, 0).setType(CellType.WATER);
        engine.world.getCellByCellId(0, 1).setType(CellType.FOREST);
        engine.world.getCellByCellId(0, 2).setType(CellType.GRASS);

        for (int i = 0; i < 5; i++) {
            expand(engine, CellType.WATER);
        }
        shrink(engine, CellType.WATER, CellType.SAND);
        shrink(engine, CellType.WATER, CellType.SHALLOW_WATER);

        setupFood(engine);

    }

    protected void setupFood(Engine engine) {
        NoiseGenerator foodNoise = new NoiseGenerator();

        NoiseLayer layer = null;
//        layer = foodNoise.addLayer();
//        layer.setDx(1000);
//        layer.setDy(1000);
//        layer.octavesX = 0.5f;
//        layer.octavesY = 0.5f;
//        layer.setThreshold(0.65f);

        layer = foodNoise.addLayer();
        layer.setDx(1000);
        layer.setDy(1000);
        layer.octavesX = engine.config.cellsPerChunkX / 3f;
        layer.octavesY = engine.config.cellsPerChunkY / 3f;
//        layer.setThreshold(0.7f);

//        forest.setMaxThreshold(0.47f);

        for (int x = 0; x < engine.config.cellsPerChunkX * engine.config.chunksX; x++) {
            for (int y = 0; y < engine.config.cellsPerChunkY * engine.config.chunksY; y++) {
                Cell oldCell = engine.world.getCellByCellId(x, y);

                float noise = foodNoise.getValue(x / (float) engine.config.cellsPerChunkX, y / (float) engine.config.cellsPerChunkY);
                boolean solid = noise > 0.9;
                if (solid) {
                    Cell c = engine.world.getCellByCellId(x, y);
                    if (c != null && !c.isRoad && c.type == CellType.GRASS) {
                        c.setType(CellType.BERRY_BUSH, CellType.BERRY_BUSH.variants - 1);
                    }
                }
            }
        }
    }

    protected void drawRandomRoads(Engine engine) {
        int roadCount = engine.config.chunksX * 20;
        Array<Cell> source = new Array<>();
        Array<Cell> dest = new Array<>();
        Vector2 pos = new Vector2();
        Vector2 delta = new Vector2();

        float chunkRange = MathUtils.clamp(engine.config.chunksX / 10, 1, 100);
        for (int i = 0; i < roadCount; i++) {
            engine.random.randomPoint(engine.world.boundary, pos);
            source.add(engine.world.getCellAtWorldPosition(pos));
            delta.set(engine.config.cellsPerChunkX * engine.config.cellSize * chunkRange, engine.config.cellsPerChunkY * engine.config.cellSize * chunkRange);
            delta.scl(engine.random.randomSign() * engine.random.random(0.2f, 1f));
            pos.add(delta);
            dest.add(engine.world.getCellAtWorldPosition(pos));
        }

        //Do path finding
        for (int i = 0; i < roadCount; i++) {
            PathFinder p = new PathFinder(engine);

            Cell start = source.get(i);
            Cell end = dest.get(i);
            PathFindingResponse response = new PathFindingResponse();
            p.getPathFindingResponse(start, end, response);
            for (Cell c : response.path) {
                if (countRoadsNear(engine, c) <= 3) {
                    c.isRoad = true;
                    c.type = CellType.GRASS;
                }
            }
        }

    }

    protected void shrink(Engine engine, CellType cellType, CellType replaceValue) {
        Array<Cell> targetCells = new Array<>(2048);
        for (int x = 0; x < engine.config.cellsPerChunkX * engine.config.chunksX; x++) {
            for (int y = 0; y < engine.config.cellsPerChunkY * engine.config.chunksY; y++) {
                Cell cell = engine.world.getCellByCellId(x, y);
                if (cell != null && cell.type == cellType && countNear(engine, cell, cellType) < 8) {
                    targetCells.add(cell);
                }
            }
        }

        for (Cell c : targetCells) {
            c.setType(replaceValue);
        }
    }

    protected void expand(Engine engine, CellType nearCellType) {
        expand(engine, null, nearCellType, 0);
    }

    protected void expand(Engine engine, CellType targetCellType, CellType nearCellType, int newVariant) {
        Array<Cell> targetCells = new Array<>(2048);
        for (int x = 0; x < engine.config.cellsPerChunkX * engine.config.chunksX; x++) {
            for (int y = 0; y < engine.config.cellsPerChunkY * engine.config.chunksY; y++) {
                Cell cell = engine.world.getCellByCellId(x, y);
                if (cell != null) {
                    if (isNextToCellOfType(engine, cell, nearCellType, engine.random.randomBoolean())) {
                        if (targetCellType == null || cell.type == targetCellType) {
                            targetCells.add(cell);
                        }
                    }
                }

            }
        }

        for (Cell c : targetCells) {
            c.setType(nearCellType, newVariant);
        }
    }

    protected void replaceNear(Engine engine, CellType nearCellType, CellType cellTypeToOverride, CellType replace) {
        for (int x = 0; x < engine.config.cellsPerChunkX * engine.config.chunksX; x++) {
            for (int y = 0; y < engine.config.cellsPerChunkY * engine.config.chunksY; y++) {
                Cell cell = engine.world.getCellByCellId(x, y);
                if (cell != null && cell.type == cellTypeToOverride) {
                    if (isNextToCellOfType(engine, cell, nearCellType, true)) {
                        cell.setType(replace);
                    }
                }
            }
        }
    }

    protected boolean isNextToCellOfType(Engine engine, Cell cell, CellType type, boolean diagonals) {
        for (int x = -1; x <= +1; x++) {
            for (int y = -1; y <= +1; y++) {
                if (!(x == 0 && y == 0) && (x * y == 0 || diagonals)) {
                    Cell other = engine.world.getCellByCellId(cell.cellId.x + x, cell.cellId.y + y);
                    if (other != null && other.type == type) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected int countNear(Engine engine, Cell cell, CellType type) {
        int count = 0;
        for (int x = -1; x <= +1; x++) {
            for (int y = -1; y <= +1; y++) {
                if (!(x == 0 && y == 0)) {
                    Cell other = engine.world.getCellByCellId(cell.cellId.x + x, cell.cellId.y + y);
                    if (other != null && other.type == type) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    protected int countRoadsNear(Engine engine, Cell cell) {
        int count = 0;
        for (int x = -1; x <= +1; x++) {
            for (int y = -1; y <= +1; y++) {
                if (!(x == 0 && y == 0)) {
                    Cell other = engine.world.getCellByCellId(cell.cellId.x + x, cell.cellId.y + y);
                    if (other != null && other.isRoad) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    protected void setupBuildings(Engine engine) {
        engine.log("MapGenerator.setupBuildings", "Setting up Engine");
        World world = engine.world;
        Rectangle region = engine.world.boundary;
        EngineSetupConfig config = engine.config;
        for (int town = 0; town < config.towns; town++) {
            Array<Entity> buildings = new Array();
            // Setup Castle
            Array<Cell> startCells = new Array();
            startCells.clear();
            Cell startCell = engine.world.cellSearcher.randomSearchMontiCarlo(
                    region.x + region.width / 2,
                    region.y + region.height / 2,
                    region.width * 2,
                    CellType.GRASS,
                    1,
                    startCells);
            Entity castle = engine.entityFactory.setupBuilding("Building", BuildingType.CASTLE, startCells.get(0).pos, world.engine.config.cellSize);
            world.addEntity(castle);

            // Setup Buildings
            startCells.clear();
            world.cellSearcher.randomSearchMontiCarlo(
                    castle.pos.x,
                    castle.pos.y,
                    config.cellSize * config.cellsPerChunkX * MathUtils.clamp(config.chunksX / 3f, 1, 2),
                    CellType.GRASS,
                    config.buildings,
                    startCells
            );
            for (int i = 0; i < config.buildings; i++) {
                Entity entity = engine.entityFactory.setupBuilding("Building", BuildingType.SMALL_HOUSE, startCells.get(i).pos, world.engine.config.cellSize);
                world.addEntity(entity);
                buildings.add(entity);

                //Do path finding
                PathFinder p = new PathFinder(world.engine);
                Cell start = castle.getCurrentCell().getLink(0, -1);
                Cell end = entity.getCurrentCell().getLink(0, -1);
                PathFindingResponse response = new PathFindingResponse();
                p.getPathFindingResponse(start, end, response);
                for (Cell c : response.path) {
                    c.isRoad = true;
                }
            }
        }
    }

    protected void setupEntities(Engine engine) {
        engine.log("MapGenerator.setupEntities", "Setting up Engine");
        World world = engine.world;
        Rectangle region = engine.world.boundary;
        EngineSetupConfig config = engine.config;

        // Setup Castle
        Array<Cell> startCells = new Array();

        world.cellSearcher.randomSearchScan(
                region.x + region.width / 2,
                region.y + region.height / 2,
                config.cellSize * config.cellsPerChunkX * config.chunksX,
                CellType.GRASS,
                config.entities,
                startCells
        );
        for (int i = 0; i < config.entities; i++) {
            engine.entityFactory.setupHuman("Basic Human", HumanType.MAN, startCells.get(i).pos, world.engine.config.cellSize);
        }
    }

    @Override
    public void createMap(Engine engine) {
        generateMap(engine);
        setupBuildings(engine);
        setupEntities(engine);
    }
}
