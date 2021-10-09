package com.emptypocketstudios.boardgame.engine.world.map;

public class MapFactory {

    public static MapGenerator getGenerator(MapGeneratorType type) {
        switch (type) {
            case BASIC:
                return new BasicMapGenerator();
            case FLAT_LAND:
                return new FlatMapGenerator();
            case TEST:
                return new TestMapGenerator();
            case CROSS_ROAD:
                return new CrossRoadMapGenerator();
            default:
                return null;
        }
    }
}
