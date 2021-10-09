package com.emptypocketstudios.boardgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.EngineSetupConfig;
import com.emptypocketstudios.boardgame.engine.world.map.MapGeneratorType;

public class TestUtil {

    public static Engine setupTestWorld() {
        EngineSetupConfig config = new EngineSetupConfig();
        config.entities = 0;
        config.towns = 0;
        config.buildings = 0;
        config.chunksX = 2;
        config.chunksY = 2;
        config.cellsPerChunkX = 5;
        config.cellsPerChunkY = 5;
        config.map = MapGeneratorType.FLAT_LAND;
        return setupTestWorld(config);
    }

    public static Engine setupTestWorld(EngineSetupConfig config) {
        Gdx.files = new HeadlessFiles();
        Engine engine = new Engine();
        engine.setup(config);
        engine.update(0);
        return engine;
    }
}
