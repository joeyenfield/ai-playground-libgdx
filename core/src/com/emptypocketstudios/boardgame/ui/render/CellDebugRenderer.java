package com.emptypocketstudios.boardgame.ui.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;
import com.emptypocketstudios.boardgame.library.ShapeRenderUtilDrawer;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class CellDebugRenderer extends BaseCellDebugRenderer {
    float triangleSize = 0.3f;

    float textHeightFractionOfCell = 1/15f;
    float textHeightScale = 1.8f;
    public float minCellSizeToDisplayTextDebug = 150f;

    public float minCellSizeToDisplayBoundaryDebug = 100f;
    public float minCellSizeToDisplayRegionDebug = 20f;

    boolean drawTriangles = true;
    boolean drawTrianglesAllowed = true;
    boolean drawTrianglesBlocked = true;

    Vector2 topPos = new Vector2();
    Vector2 rightPos = new Vector2();
    Vector2 downPos = new Vector2();
    Vector2 leftPos = new Vector2();

    Color drawColor = new Color();
    ShapeRenderUtilDrawer textDrawer;

    Vector2 textPos = new Vector2();

    public CellDebugRenderer(ShapeDrawer drawer) {
        super(drawer);
        this.wallSize = 0.05f;
        textDrawer = new ShapeRenderUtilDrawer();
    }

    public void drawCellBoundary(Engine engine, Cell cell) {
        if (!shouldDrawCell(cell, minCellSizeToDisplayBoundaryDebug)) {
            return;
        }
        float boundaryWallX = wallSize * cell.boundary.width;
        float boundaryWallY = wallSize * cell.boundary.height;
        drawer.setColor(getCellColor(engine, cell));
        drawTopBoundary(cell, boundaryWallX, boundaryWallY);
        drawBottomBoundary(cell, boundaryWallX, boundaryWallY);
        drawLeftBoundary(cell, boundaryWallX, boundaryWallY);
        drawRightBoundary(cell, boundaryWallX, boundaryWallY);
    }

    public void drawCellText(Engine engine, Cell cell) {
        if (!shouldDrawCell(cell, minCellSizeToDisplayTextDebug)) {
            return;
        }
        float textHeight = cell.boundary.height*textHeightFractionOfCell;

        drawer.setColor(Color.BLACK);

        textPos.set(cell.boundary.x+textHeight, cell.boundary.y+textHeight);
        textDrawer.render(drawer, " Type:" + cell.type.name(), textPos, textHeight, viewportBounds);
        textPos.y += textHeightScale * textHeight;
        textDrawer.render(drawer, "VType:" + cell.typeVariant, textPos, textHeight, viewportBounds);
        textPos.y += textHeightScale * textHeight;
        textDrawer.render(drawer, "CTime:" + cell.lastChangeTime, textPos, textHeight/2, viewportBounds);
        textPos.y += textHeightScale * textHeight;
        textDrawer.render(drawer, " Road:" + cell.isRoad, textPos, textHeight, viewportBounds);
    }

    public void drawLineCells(Cell cellA, Cell cellB, Color c, float thick) {
        if (!(shouldDrawCell(cellA, minCellSizeToDisplayBoundaryDebug) || shouldDrawCell(cellB, minCellSizeToDisplayBoundaryDebug))) {
            return;
        }
        drawer.line(cellA.pos, cellB.pos, c, lineSize * thick);
    }

    public void drawRegionDebug(Engine engine, Cell cell) {
        if (!shouldDrawCell(cell, minCellSizeToDisplayRegionDebug)) {
            return;
        }
        drawer.setColor(getCellColor(engine, cell));
        drawColor.set(getCellColor(engine, cell));
        drawColor.lerp(Color.BLACK, 0.3f);

        float boundaryWallX = wallSize * cell.boundary.width;
        float boundaryWallY = wallSize * cell.boundary.height;
        float insetX = triangleSize * cell.boundary.width;
        float insetY = triangleSize * cell.boundary.height;
        setupPoints(cell, insetX, insetY);
        drawUpRegionTriangles(engine, cell, boundaryWallX, boundaryWallY, insetX, insetY);
        drawDownRegionTriangles(engine, cell, boundaryWallX, boundaryWallY, insetX, insetY);
        drawLeftRegionTriangles(engine, cell, boundaryWallX, boundaryWallY, insetX, insetY);
        drawRightRegionTriangles(engine, cell, boundaryWallX, boundaryWallY, insetX, insetY);
    }

    public void update(Rectangle viewportBounds, float lineSize) {
        this.viewportBounds.set(viewportBounds);
        this.lineSize = lineSize;
    }

    private boolean shouldDrawCell(Cell cell, float minPixelSize) {
        if (cell == null || !viewportBounds.overlaps(cell.boundary)) {
            return false;
        }
        if (cell.boundary.width / lineSize <= minPixelSize) {
            return false;
        }
        return true;
    }

    private void setupPoints(Cell cell, float insetX, float insetY) {
        topPos.x = cell.boundary.x + cell.boundary.width / 2;
        topPos.y = cell.boundary.y + cell.boundary.height - insetY;
        rightPos.x = cell.boundary.x + cell.boundary.width - insetX;
        rightPos.y = cell.boundary.y + cell.boundary.height / 2;
        downPos.x = cell.boundary.x + cell.boundary.width / 2;
        downPos.y = cell.boundary.y + insetY;
        leftPos.x = cell.boundary.x + insetX;
        leftPos.y = cell.boundary.y + cell.boundary.height / 2;
    }

    private void movePoints(float dx, float dy) {
        leftPos.add(dx, dy);
        topPos.add(dx, dy);
        rightPos.add(dx, dy);
        downPos.add(dx, dy);
    }

    private boolean hasRegionLink(Engine engine, Cell cell, Cell other) {
        Array<RegionNode> regions = engine.world.regionLinks.get(cell.region);
        if (regions == null) {
            return false;
        }
        return regions.contains(other.region, false);
    }

    private void drawRightRegionTriangles(Engine engine, Cell cell, float boundaryWallX, float boundaryWallY, float insetX, float insetY) {
        Cell right = cell.getLink(1, 0);
        if (right != null) {
            if (!right.region.equals(cell.region)) {
                if (drawTriangles) {
                    float dx = insetX - boundaryWallX;
                    float dy = 0;
                    movePoints(dx, dy);
                    if (hasRegionLink(engine, cell, right)) {
                        if (drawTrianglesAllowed) drawer.filledTriangle(topPos, rightPos, downPos);
                    } else {
                        if (drawTrianglesBlocked) drawer.triangle(topPos, rightPos, downPos);
                    }
                    movePoints(-dx, -dy);
                }

                drawRightBoundary(cell, boundaryWallX, boundaryWallY);
            }
        }
    }

    private void drawLeftRegionTriangles(Engine engine, Cell cell, float boundaryWallX, float boundaryWallY, float insetX, float insetY) {
        Cell left = cell.getLink(-1, 0);
        if (left != null) {
            if (!left.region.equals(cell.region)) {
                if (drawTriangles) {
                    float dx = -insetX + boundaryWallX;
                    float dy = 0;
                    movePoints(dx, dy);
                    if (hasRegionLink(engine, cell, left)) {
                        if (drawTrianglesAllowed) drawer.filledTriangle(downPos, leftPos, topPos);
                    } else {
                        if (drawTrianglesBlocked) drawer.triangle(downPos, leftPos, topPos);
                    }
                    movePoints(-dx, -dy);
                }
                drawLeftBoundary(cell, boundaryWallX, boundaryWallY);
            }
        }
    }

    private void drawUpRegionTriangles(Engine engine, Cell cell, float boundaryWallX, float boundaryWallY, float insetX, float insetY) {
        Cell up = cell.getLink(0, 1);
        if (up != null) {
            if (!up.region.equals(cell.region)) {
                if (drawTriangles) {
                    float dx = 0;
                    float dy = insetY - boundaryWallY;
                    movePoints(dx, dy);
                    if (hasRegionLink(engine, cell, up)) {
                        if (drawTrianglesAllowed) drawer.filledTriangle(leftPos, topPos, rightPos);
                    } else {
                        if (drawTrianglesBlocked) drawer.triangle(leftPos, topPos, rightPos);
                    }
                    movePoints(-dx, -dy);
                }

                drawTopBoundary(cell, boundaryWallX, boundaryWallY);

            }
        }
    }

    private void drawDownRegionTriangles(Engine engine, Cell cell, float boundaryWallX, float boundaryWallY, float insetX, float insetY) {
        Cell down = cell.getLink(0, -1);
        if (down != null) {
            if (!down.region.equals(cell.region)) {
                if (drawTriangles) {
                    float dx = 0;
                    float dy = -insetY + boundaryWallY;
                    movePoints(dx, dy);
                    if (hasRegionLink(engine, cell, down)) {
                        if (drawTrianglesAllowed) drawer.filledTriangle(leftPos, downPos, rightPos);
                    } else {
                        if (drawTrianglesBlocked) drawer.triangle(leftPos, downPos, rightPos);
                    }
                    movePoints(-dx, -dy);
                }

                drawBottomBoundary(cell, boundaryWallX, boundaryWallY);
            }
        }
    }
}
