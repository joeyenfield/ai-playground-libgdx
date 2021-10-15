package com.emptypocketstudios.boardgame.ui.render;

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;

import java.util.HashMap;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class CellTextureRenderer {
    HashMap<Integer, TextureAtlas.AtlasRegion> textures = new HashMap<>();

    ShapeDrawer drawer;
    Rectangle viewportBounds = new Rectangle();
    float lineSize;
    TextureAtlas atlas;

    public CellTextureRenderer(TextureAtlas atlas, ShapeDrawer drawer) {
        this.drawer = drawer;
        this.atlas = atlas;
        setupAtlas(atlas);
    }

    public TextureAtlas.AtlasRegion getRoadTexture(Cell c) {
        boolean up = c.isLinkCellRoad(0, 1);
        boolean down = c.isLinkCellRoad(0, -1);
        boolean left = c.isLinkCellRoad(-1, 0);
        boolean right = c.isLinkCellRoad(1, 0);
        return getTexture(getRoadOffset(up, down, left, right));
    }

    public TextureAtlas.AtlasRegion getTexture(Cell c) {
        Integer cellId = c.type.cellId + c.typeVariant;
        return getTexture(cellId);
    }

    public TextureAtlas.AtlasRegion getTexture(int cellId) {
        if (textures.containsKey(cellId)) {
            return textures.get(cellId);
        } else {
            return textures.get(CellType.EMPTY);
        }
    }

    public void setupAtlas(TextureAtlas atlas) {
        textures.put(CellType.EMPTY.cellId, atlas.findRegion("Tile/medievalTile", 56));
        textures.put(CellType.DIRT.cellId, atlas.findRegion("Tile/medievalTile", 13));
        textures.put(CellType.SAND.cellId, atlas.findRegion("Tile/medievalTile", 1));
        textures.put(CellType.ROCK.cellId, atlas.findRegion("Tile/medievalTile", 15));
        textures.put(CellType.GRASS.cellId, atlas.findRegion("Tile/medievalTile", 57));
        textures.put(CellType.WATER.cellId, atlas.findRegion("Tile/medievalTile", 27));
        textures.put(CellType.SHALLOW_WATER.cellId, atlas.findRegion("Tile/medievalTile", 29));
        textures.put(CellType.ICE.cellId, atlas.findRegion("Tile/medievalTile", 29));

        textures.put(CellType.FOREST.cellId, atlas.findRegion("Tile/medievalTile", 41));
        textures.put(CellType.FOREST.cellId + 1, atlas.findRegion("Tile/medievalTile", 42));
        textures.put(CellType.FOREST.cellId + 2, atlas.findRegion("Tile/medievalTile", 43));
        textures.put(CellType.FOREST.cellId + 3, atlas.findRegion("Tile/medievalTile", 44));

        textures.put(CellType.CHOPPED_FOREST.cellId, atlas.findRegion("Tile/medievalTile", 49));
        textures.put(CellType.CHOPPED_FOREST.cellId + 1, atlas.findRegion("Tile/medievalTile", 50));
        textures.put(CellType.CHOPPED_FOREST.cellId + 2, atlas.findRegion("Tile/medievalTile", 51));
        textures.put(CellType.CHOPPED_FOREST.cellId + 3, atlas.findRegion("Tile/medievalTile", 52));

        textures.put(CellType.BERRY_BUSH.cellId, atlas.findRegion("Tile/medievalTile", 65));
        textures.put(CellType.BERRY_BUSH.cellId + 1, atlas.findRegion("Tile/medievalTile", 66));
        textures.put(CellType.BERRY_BUSH.cellId + 2, atlas.findRegion("Tile/medievalTile", 67));
        textures.put(CellType.BERRY_BUSH.cellId + 3, atlas.findRegion("Tile/medievalTile", 68));
        textures.put(CellType.BERRY_BUSH.cellId + 4, atlas.findRegion("Tile/medievalTile", 69));
        textures.put(CellType.BERRY_BUSH.cellId + 5, atlas.findRegion("Tile/medievalTile", 70));


        textures.put(CellType.BUILDING_FOUNDATION.cellId, atlas.findRegion("Tile/medievalTile", 13));
        int roadOffset = 5;
        textures.put(getRoadOffset(true, true, false, false), atlas.findRegion("Tile/medievalTile", (3 + roadOffset)));
        textures.put(getRoadOffset(false, false, true, true), atlas.findRegion("Tile/medievalTile", (4 + roadOffset)));
        textures.put(getRoadOffset(true, true, true, true), atlas.findRegion("Tile/medievalTile", (5 + roadOffset)));
        textures.put(getRoadOffset(false, true, true, true), atlas.findRegion("Tile/medievalTile", (6 + roadOffset)));
        textures.put(getRoadOffset(true, false, true, true), atlas.findRegion("Tile/medievalTile", (7 + roadOffset)));
        textures.put(getRoadOffset(false, true, false, true), atlas.findRegion("Tile/medievalTile", (17 + roadOffset)));
        textures.put(getRoadOffset(false, true, true, false), atlas.findRegion("Tile/medievalTile", (18 + roadOffset)));
        textures.put(getRoadOffset(false, false, true, false), atlas.findRegion("Tile/medievalTile", (19 + roadOffset)));
        textures.put(getRoadOffset(true, true, true, false), atlas.findRegion("Tile/medievalTile", (20 + roadOffset)));
        textures.put(getRoadOffset(true, true, false, true), atlas.findRegion("Tile/medievalTile", (21 + roadOffset)));
        textures.put(getRoadOffset(true, false, false, true), atlas.findRegion("Tile/medievalTile", (31 + roadOffset)));
        textures.put(getRoadOffset(true, false, true, false), atlas.findRegion("Tile/medievalTile", (32 + roadOffset)));
        textures.put(getRoadOffset(false, false, false, true), atlas.findRegion("Tile/medievalTile", (33 + roadOffset)));
        textures.put(getRoadOffset(true, false, false, false), atlas.findRegion("Tile/medievalTile", (34 + roadOffset)));
        textures.put(getRoadOffset(false, true, false, false), atlas.findRegion("Tile/medievalTile", (35 + roadOffset)));
        textures.put(getRoadOffset(false, false, false, false), atlas.findRegion("Tile/medievalTile", (59 + roadOffset)));
    }

    /**
     * Works out which road tile to show using a bit mask
     *
     * @param up
     * @param down
     * @param left
     * @param right
     * @return
     */
    public static int getRoadOffset(boolean up, boolean down, boolean left, boolean right) {
        int value = 0;
        if (up) value = value | 0b1;
        if (down) value = value | 0b10;
        if (left) value = value | 0b100;
        if (right) value = value | 0b1000;

        return -500 + value;
    }

    public void update(Rectangle viewportBounds, float lineSize) {
        this.viewportBounds.set(viewportBounds);
        this.lineSize = lineSize;
    }


    public void drawCell(Engine engine, Cell cell) {
        if (cell == null || !viewportBounds.overlaps(cell.boundary)) {
            return;
        }
        float padding = 1.01f;
        PolygonSpriteBatch batch = (PolygonSpriteBatch) drawer.getBatch();
        TextureAtlas.AtlasRegion region = getTexture(cell);
        batch.draw(region,
                cell.boundary.x,
                cell.boundary.y,
                cell.boundary.width * padding,
                cell.boundary.height * padding);

        if (cell.isRoad) {
            region = getRoadTexture(cell);
            batch.draw(region,
                    cell.boundary.x,
                    cell.boundary.y,
                    cell.boundary.width * padding,
                    cell.boundary.height * padding);
        }
    }
}
