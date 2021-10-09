package com.emptypocketstudios.boardgame.engine.world.map;

import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.world.CellType;

public class FlatMapGenerator extends MapGenerator {
    @Override
    public void createMap(Engine engine) {
        engine.world.fillAllCells(CellType.GRASS);
    }
}
