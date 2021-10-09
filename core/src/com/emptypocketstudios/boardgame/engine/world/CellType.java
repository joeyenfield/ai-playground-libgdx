package com.emptypocketstudios.boardgame.engine.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public enum CellType {

    //    HashMap<Integer, TextureAtlas.AtlasRegion> textures = new java.util.HashMap<>();
    EMPTY(false, 0),
    DIRT(false, 10),
    SAND(false, 20),
    ROCK(false, 30),
    GRASS(false, 40),
    WATER(true, 50),
    SHALLOW_WATER(false, 55),
    ICE(false, 60),
    //    ROAD(false, 100),
    FOREST(false, 200, 4),
    CHOPPED_FOREST(false, 250, 4),
    BUILDING_FOUNDATION(false, 300),
    BERRY_BUSH(false, 350, 6);

    public static final float ROAD_FACTOR = 0.4f;
    public static final float BLOCKED_FACTOR = 1e10f;
    public final boolean blocked;
    public final Integer cellId;
    public final Integer variants;

    CellType(boolean blocked, Integer ids) {
        this(blocked, ids, 1);
    }

    CellType(boolean blocked, Integer ids, int variants) {
        this.blocked = blocked;
        this.cellId = ids;
        this.variants = variants;
    }

    public static Array<String> names() {
        Array<String> names = new Array<>();
        for (CellType ct : CellType.values()) {
            names.add(ct.name());
        }
        return names;
    }

    public static float getTravelEffort(Cell c) {
        return c.type.getTravelEffort(c.typeVariant, c.isRoad);
    }

    public float getTravelEffort(int variant, boolean isRoad) {
        float result = 1;
        switch (this) {
            case FOREST:
                result = 1 + (1F * MathUtils.map(0, FOREST.variants - 1, 0, 1.5f, variant));
                break;
            case CHOPPED_FOREST:
                result = 1.2F;
                break;
            case SAND:
                result = 1.4f;
                break;
            case DIRT:
                result = 1.2f;
                break;
            case BUILDING_FOUNDATION:
                result = 2f / ROAD_FACTOR; //Should be road so /2
                break;
            default:
                result = 1;
        }
        if (blocked) {
            result *= BLOCKED_FACTOR;
        }
        if (isRoad) {
            result *= ROAD_FACTOR;
        }
        return result;
    }
}
