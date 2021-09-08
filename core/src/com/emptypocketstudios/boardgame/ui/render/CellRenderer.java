package com.emptypocketstudios.boardgame.ui.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellTypes;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;
import com.emptypocketstudios.boardgame.library.ColorMap;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class CellRenderer {
    public boolean drawAllCells = true;
    public boolean drawAllCellsBounds = true;
    public boolean drawAllCellLinks = false;

    ShapeDrawer drawer;
    Rectangle viewportBounds = new Rectangle();
    float lineSize;
    TextureAtlas atlas;

    public CellRenderer(TextureAtlas atlas, ShapeDrawer drawer) {
        this.drawer = drawer;
        this.atlas = atlas;
    }

    public void update(Rectangle viewportBounds, float lineSize) {
        this.viewportBounds.set(viewportBounds);
        this.lineSize = lineSize;
    }

    public Color getCellColor(Engine e, Cell c) {
        if (CellTypes.isBlocked(c.type)) {
            return Color.GRAY;
        }
        return ColorMap.getRandomColor((byte) (c.region.hashCode()));
    }

    public void textureCell(Engine engine, Cell cell) {
        if (cell == null || !viewportBounds.overlaps(cell.boundary)) {
            return;
        }
        if (drawAllCells) {
            float padding = 1.01f;
            PolygonSpriteBatch batch = (PolygonSpriteBatch) drawer.getBatch();
            TextureAtlas.AtlasRegion region = CellTypes.getTexture(cell);
            batch.draw(region,
                    cell.boundary.x, cell.boundary.y,
                    cell.boundary.width * padding, cell.boundary.height * padding);
        }
    }

    public void renderCell(Engine engine, Cell cell) {
        if (cell == null || !viewportBounds.overlaps(cell.boundary)) {
            return;
        }
        if (cell.boundary.width / lineSize <= 2) {
            return;
        }
        if (drawAllCells) {
            drawer.rectangle(cell.boundary, getCellColor(engine, cell));
        }

        if (cell.boundary.width / lineSize <= 10) {
            return;
        }

        if (drawAllCellLinks) {
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    Cell c = cell.links[x][y];
                    if (c != null) {
                        drawLineCells(cell, c, Color.RED, 1);
                    }
                }
            }
        }

//        if (drawAllCells) {
//            Color lineColor = engine.regions.contains(cell.region, false) ? Color.RED : Color.BLACK;
//            drawer.rectangle(cell.boundary, lineColor, lineSize);
//        }
    }


    public void drawLineCells(Cell cellA, Cell cellB, Color c, float thick) {
        drawer.line(cellA.pos, cellB.pos, c, lineSize * thick);
    }

}
