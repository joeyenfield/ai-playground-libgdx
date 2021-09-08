package com.emptypocketstudios.boardgame.engine.world;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class CellTypes {
    public static HashMap<Integer, TextureAtlas.AtlasRegion> textures = new HashMap<>();
    public static short EMPTY = 0;
    public static final short DIRT = 10;
    public static final short SAND = 20;
    public static final short ROCK = 30;
    public static final short GRASS = 40;
    public static final short WATER = 50;
    public static final short ICE = 60;
    public static final short ROAD = 100;
    public static final short FOREST = 200;

    public static short getId(String name) {
        switch (name) {
            case "DIRT":
                return DIRT;
            case "SAND":
                return SAND;
            case "ROCK":
                return ROCK;
            case "GRASS":
                return GRASS;
            case "WATER":
                return WATER;
            case "ROAD":
                return ROAD;
            case "ICE":
                return ICE;
            case "FOREST":
                return FOREST;
        }
        return EMPTY;
    }

    public static String name(short type) {
        switch (type) {
            case DIRT:
                return "DIRT";
            case SAND:
                return "SAND";
            case ROCK:
                return "ROCK";
            case GRASS:
                return "GRASS";
            case WATER:
                return "WATER";
            case ROAD:
                return "ROAD";
            case ICE:
                return "ICE";
            case FOREST:
                return "FOREST";
        }
        return "";
    }

    public static Array<String> names() {
        Array<String> names = new Array<>();
        names.add(name(DIRT));
        names.add(name(SAND));
        names.add(name(ROCK));
        names.add(name(GRASS));
        names.add(name(WATER));
        names.add(name(ICE));
        names.add(name(ROAD ));
        names.add(name(FOREST ));
        return names;
    }

    public static float getTravelEffort(short type) {
        switch (type) {
            case ROAD:
                return 0.6f;
            case FOREST:
                return 3F;
            case SAND:
                return 1.4f;
            case DIRT:
                return 1.2f;
            default:
                return 1;
        }
    }

    public static boolean isBlocked(short type) {
        switch (type) {
            case WATER:
            case ROCK:
                return true;
            default:
                return false;
        }
    }

    public static TextureAtlas.AtlasRegion getTexture(Cell c) {

        int cellId = 0;
        if (c.type == ROAD) {
            boolean up = c.isLinkCellType(0, 1, ROAD);
            boolean down = c.isLinkCellType(0, -1, ROAD);
            boolean left = c.isLinkCellType(-1, 0, ROAD);
            boolean right = c.isLinkCellType(1, 0, ROAD);
            cellId = ROAD + getRoadOffset(up, down, left, right);
        }else{
            cellId = c.type;
        }

        if (textures.containsKey(cellId)) {
            return textures.get(cellId);
        } else {
            return textures.get(EMPTY);
        }
    }

    public static void setupAtlas(TextureAtlas atlas) {
        textures.put(+EMPTY, atlas.findRegion("Tile/medievalTile", 56));

        textures.put(DIRT + 0, atlas.findRegion("Tile/medievalTile", 13));
        textures.put(DIRT + 1, atlas.findRegion("Tile/medievalTile", 14));

        textures.put(SAND + 0, atlas.findRegion("Tile/medievalTile", 1));
        textures.put(SAND + 1, atlas.findRegion("Tile/medievalTile", 2));

        textures.put(ROCK + 0, atlas.findRegion("Tile/medievalTile", 15));
        textures.put(ROCK + 1, atlas.findRegion("Tile/medievalTile", 16));

        textures.put(GRASS + 0, atlas.findRegion("Tile/medievalTile", 57));
        textures.put(GRASS + 1, atlas.findRegion("Tile/medievalTile", 58));

        textures.put(WATER + 0, atlas.findRegion("Tile/medievalTile", 27));
        textures.put(WATER + 1, atlas.findRegion("Tile/medievalTile", 28));

        textures.put(ICE + 0, atlas.findRegion("Tile/medievalTile", 29));
        textures.put(ICE + 1, atlas.findRegion("Tile/medievalTile", 30));

        textures.put(+FOREST, atlas.findRegion("Tile/medievalTile", 44));


        textures.put(ROAD + getRoadOffset(false, false, false, false)
                , atlas.findRegion("Tile/medievalTile", 59));

        textures.put(ROAD + getRoadOffset(true, false, false, false)
                , atlas.findRegion("Tile/medievalTile", 34));
        textures.put(ROAD + getRoadOffset(false, true, false, false)
                , atlas.findRegion("Tile/medievalTile", 35));
        textures.put(ROAD + getRoadOffset(false, false, true, false)
                , atlas.findRegion("Tile/medievalTile", 19));
        textures.put(ROAD + getRoadOffset(false, false, false, true)
                , atlas.findRegion("Tile/medievalTile", 33));


        textures.put(ROAD + getRoadOffset(true, true, false, false)
                , atlas.findRegion("Tile/medievalTile", 3));
        textures.put(ROAD + getRoadOffset(false, false, true, true)
                , atlas.findRegion("Tile/medievalTile", 4));
        textures.put(ROAD + getRoadOffset(true, true, true, true)
                , atlas.findRegion("Tile/medievalTile", 5));
        textures.put(ROAD + getRoadOffset(false, true, true, true)
                , atlas.findRegion("Tile/medievalTile", 6));
        textures.put(ROAD + getRoadOffset(true, false, true, true)
                , atlas.findRegion("Tile/medievalTile", 7));

        textures.put(ROAD + getRoadOffset(false, true, false, true)
                , atlas.findRegion("Tile/medievalTile", 17));
        textures.put(ROAD + getRoadOffset(false, true, true, false)
                , atlas.findRegion("Tile/medievalTile", 18));

        textures.put(ROAD + getRoadOffset(true, true, true, false)
                , atlas.findRegion("Tile/medievalTile", 20));

        textures.put(ROAD + getRoadOffset(true, true, false, true)
                , atlas.findRegion("Tile/medievalTile", 21));

        textures.put(ROAD + getRoadOffset(true, false, false, true)
                , atlas.findRegion("Tile/medievalTile", 31));

        textures.put(ROAD + getRoadOffset(true, false, true, false)
                , atlas.findRegion("Tile/medievalTile", 32));
    }

    public static int getRoadOffset(boolean up, boolean down, boolean left, boolean right) {
        int value = 0;
        if (up) value = value | 0b1;
        if (down) value = value | 0b10;
        if (left) value = value | 0b100;
        if (right) value = value | 0b1000;

        return value;
    }


}
