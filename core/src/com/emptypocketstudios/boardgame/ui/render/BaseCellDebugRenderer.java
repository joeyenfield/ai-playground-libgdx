package com.emptypocketstudios.boardgame.ui.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.library.ColorMap;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class BaseCellDebugRenderer {
    float wallSize = 0.05f;
    ShapeDrawer drawer;
    Rectangle viewportBounds = new Rectangle();
    Rectangle boundaryRect = new Rectangle();
    float lineSize;

    public BaseCellDebugRenderer(ShapeDrawer drawer) {
        this.drawer = drawer;
    }

    public void update(Rectangle viewportBounds, float lineSize) {
        this.viewportBounds.set(viewportBounds);
        this.lineSize = lineSize;
    }

    public void drawTopBoundary(Cell cell, float boundaryWallX, float boundaryWallY) {
        boundaryRect.set(
                cell.boundary.x,
                cell.boundary.y + cell.boundary.height - boundaryWallY,
                cell.boundary.width,
                boundaryWallY);
        drawer.filledRectangle(boundaryRect);
    }

    public void drawBottomBoundary(Cell cell, float boundaryWallX, float boundaryWallY) {
        boundaryRect.set(
                cell.boundary.x,
                cell.boundary.y,
                cell.boundary.width,
                boundaryWallY);
        drawer.filledRectangle(boundaryRect);
    }

    public void drawLeftBoundary(Cell cell, float boundaryWallX, float boundaryWallY) {
        boundaryRect.set(
                cell.boundary.x,
                cell.boundary.y,
                boundaryWallX,
                cell.boundary.height);
        drawer.filledRectangle(boundaryRect);
    }

    public void drawRightBoundary(Cell cell, float boundaryWallX, float boundaryWallY) {
        boundaryRect.set(
                cell.boundary.x + cell.boundary.width - boundaryWallX,
                cell.boundary.y,
                boundaryWallX,
                cell.boundary.height);
        drawer.filledRectangle(boundaryRect);
    }

    public Color getCellColor(Engine e, Cell c) {
        if (c.type.blocked) {
            return Color.GRAY;
        }
        return ColorMap.getRandomColor((byte) (c.region.hashCode()));
    }
}
