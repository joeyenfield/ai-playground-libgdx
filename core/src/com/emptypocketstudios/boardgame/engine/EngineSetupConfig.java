package com.emptypocketstudios.boardgame.engine;

import com.emptypocketstudios.boardgame.engine.world.map.MapGeneratorType;

public class EngineSetupConfig {
    public int cellsPerChunkX = 16;
    public int cellsPerChunkY = 16;
    public int chunksX = 64;
    public int chunksY = 64;
    public int cellSize = 40; //pixels per cell
    public int entities = 500;
    public int towns = 64;
    public int buildings = 10;
    public long randomSeed = 126;
    public MapGeneratorType map = MapGeneratorType.BASIC;
    public boolean enableFullBTreeLogging = false;



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
