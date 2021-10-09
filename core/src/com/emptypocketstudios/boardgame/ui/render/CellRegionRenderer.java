package com.emptypocketstudios.boardgame.ui.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;
import com.emptypocketstudios.boardgame.library.ColorMap;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class CellRegionRenderer {
    float triangleSize = 0.3f;
    float wallSize = 0.05f;

    boolean drawTriangles = true;
    boolean drawTrianglesAllowed = true;
    boolean drawTrianglesBlocked = true;
    boolean drawWall = true;

    boolean limitToPathFinder = true;


    ShapeDrawer drawer;
    Rectangle viewportBounds = new Rectangle();
    float lineSize;

    public CellRegionRenderer(ShapeDrawer drawer) {
        this.drawer = drawer;
    }

    public void update(Rectangle viewportBounds, float lineSize) {
        this.viewportBounds.set(viewportBounds);
        this.lineSize = lineSize;
    }

    public Color getCellColor(Engine e, Cell c) {
        if (c.type.blocked) {
            return Color.GRAY;
        }
        return ColorMap.getRandomColor((byte) (c.region.hashCode()));
    }

    Vector2 topPos = new Vector2();
    Vector2 rightPos = new Vector2();
    Vector2 downPos = new Vector2();
    Vector2 leftPos = new Vector2();
    Rectangle rect = new Rectangle();


    protected void setupPoints(Cell cell, float insetX, float insetY) {
        topPos.x = cell.boundary.x + cell.boundary.width / 2;
        topPos.y = cell.boundary.y + cell.boundary.height - insetY;

        rightPos.x = cell.boundary.x + cell.boundary.width - insetX;
        rightPos.y = cell.boundary.y + cell.boundary.height / 2;

        downPos.x = cell.boundary.x + cell.boundary.width / 2;
        downPos.y = cell.boundary.y + insetY;

        leftPos.x = cell.boundary.x + insetX;
        leftPos.y = cell.boundary.y + cell.boundary.height / 2;
    }

    protected void movePoints(float dx, float dy) {
        leftPos.add(dx, dy);
        topPos.add(dx, dy);
        rightPos.add(dx, dy);
        downPos.add(dx, dy);
    }

    protected boolean hasRegionLink(Engine engine, Cell cell, Cell other) {
        Array<RegionNode> regions = engine.world.regionLinks.get(cell.region);
        if (regions == null) {
            return false;
        }
        return regions.contains(other.region, false);
    }

    public void renderCellRegionBoundary(Engine engine, Cell cell) {
        if (limitToPathFinder) {
            if (!(engine.pathFinderManager.pathFinder.regions.contains(cell.region, false))) {
                return;
            }
        }

        float boundaryWallX = wallSize * cell.boundary.width;
        float boundaryWallY = wallSize * cell.boundary.height;

        float insetX = triangleSize * cell.boundary.width;
        float insetY = triangleSize * cell.boundary.height;
        setupPoints(cell, insetX, insetY);

        Color color = getCellColor(engine, cell);
        drawer.setColor(color);
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

                if (drawWall) {
                    rect.set(
                            cell.boundary.x,
                            cell.boundary.y + cell.boundary.height - boundaryWallY,
                            cell.boundary.width,
                            boundaryWallY);
                    drawer.filledRectangle(rect);
                }

            }
        }

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

                if (drawWall) {
                    rect.set(
                            cell.boundary.x,
                            cell.boundary.y,
                            cell.boundary.width,
                            boundaryWallY);
                    drawer.filledRectangle(rect);
                }
            }
        }

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
                if (drawWall) {
                    rect.set(
                            cell.boundary.x,
                            cell.boundary.y,
                            boundaryWallX,
                            cell.boundary.height);
                    drawer.filledRectangle(rect);
                }
            }
        }

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

                if (drawWall) {
                    rect.set(
                            cell.boundary.x + cell.boundary.width - boundaryWallX,
                            cell.boundary.y,
                            boundaryWallX,
                            cell.boundary.height);
                    drawer.filledRectangle(rect);
                }
            }
        }
    }

}
