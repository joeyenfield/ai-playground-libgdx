package com.emptypocketstudios.boardgame.engine.world;

import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.library.noise.OpenSimplexNoise;

public class MapGenerator {

    public static void defaultMap(Engine engine) {
        float threshold = 0.4f;
        OpenSimplexNoise simpNoise = new OpenSimplexNoise();
        for (int x = 0; x < engine.cellsPerChunkX * engine.chunksX; x++) {
            for (int y = 0; y < engine.cellsPerChunkY * engine.chunksY; y++) {
                boolean solid = (simpNoise.eval(x / 2f, y / 2f) + simpNoise.eval(x / 20f, y / 20f) + simpNoise.eval(x / 10f, y / 10f)) / 2 > threshold;
                if (solid) {
                    Cell c = engine.world.getCellByCellId(x, y);
                    if (c != null) {
                        c.type = CellTypes.FOREST;
                    }
                }
            }
        }

        threshold = 0.8f;
        for (int x = 0; x < engine.cellsPerChunkX * engine.chunksX; x++) {
            for (int y = 0; y < engine.cellsPerChunkY * engine.chunksY; y++) {
                boolean solid = (simpNoise.eval(x / 2f + 3.14, y / 2f + 3.14) + simpNoise.eval(x / 20f + 3.14, y / 20f + 3.14) + simpNoise.eval(x / 10f + 3.14, y / 10f + 3.14)) / 2 > threshold;
                if (solid) {
                    Cell c = engine.world.getCellByCellId(x, y);
                    if (c != null) {
                        c.type = CellTypes.ROCK;
                    }
                }
            }
        }
    }
}
