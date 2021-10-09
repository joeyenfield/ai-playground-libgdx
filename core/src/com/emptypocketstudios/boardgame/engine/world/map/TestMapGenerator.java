package com.emptypocketstudios.boardgame.engine.world.map;

import com.badlogic.gdx.math.Vector2;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.entity.components.human.HumanType;
import com.emptypocketstudios.boardgame.engine.world.CellType;
import com.emptypocketstudios.boardgame.engine.world.World;

public class TestMapGenerator extends MapGenerator {
    @Override
    public void createMap(Engine engine) {
        World world = engine.world;
        world.fillAllCells(CellType.GRASS);
        int types = 5;
        int idx = 0;
        for (int x = 0; x < world.getCellsX(); x++) {
            if(x%2==0) {
                idx = (x % types);
            }
            for (int y = 0; y < world.getCellsY(); y++) {


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
        engine.entityFactory.setupHuman("Man", HumanType.MAN, world.getCellByCellId(0,0).pos, engine.config.cellSize);

    }
}
