package com.emptypocketstudios.boardgame.engine;

import com.emptypocketstudios.boardgame.engine.world.map.MapGeneratorType;

public class EngineSetupConfig {
    public int cellsPerChunkX = 32;
    public int cellsPerChunkY = 32;
    public int chunksX = 10;
    public int chunksY = 10;
    public int cellSize = 40; //pixels per cell
    public int entities = 1000;
    public int towns = 10;
    public int buildings = 20;
    public long randomSeed = 126;
    public MapGeneratorType map = MapGeneratorType.BASIC;
    public boolean enableFullBTreeLogging = false;
    public boolean debug =false;


//    public int cellsPerChunkX = 10;
//    public int cellsPerChunkY = 10;
//    public int chunksX = 10;
//    public int chunksY = 10;
//    public int cellSize = 40; //pixels per cell
//    public int entities = 1;
//    public int towns = 1;
//    public int buildings = 1;
//    public long randomSeed = 126;
//    public MapGeneratorType map = MapGeneratorType.CROSS_ROAD;
//    public boolean enableFullBTreeLogging = false;
}
