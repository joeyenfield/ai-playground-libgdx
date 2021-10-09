package com.emptypocketstudios.boardgame.engine.world.map;

import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.entity.components.human.HumanType;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;

public class CrossRoadMapGenerator extends MapGenerator {
    @Override
    public void createMap(Engine engine) {
        engine.world.fillAllCells(CellType.GRASS);
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
                        if (x == (engine.config.cellsPerChunkX / 2) | y == (engine.config.cellsPerChunkY / 2)) {
//                            cell.setType(CellType.GRASS, 0, true);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < engine.config.entities; i++) {
            engine.entityFactory.setupHuman("test", HumanType.MAN, world.getCellByCellId(0, 0).pos, engine.config.cellSize);
        }
        engine.update(1);
    }
}
