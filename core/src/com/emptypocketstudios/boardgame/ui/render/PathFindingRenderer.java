package com.emptypocketstudios.boardgame.ui.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.PathFollowComponent;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFinder;
import com.emptypocketstudios.boardgame.engine.pathfinding.cells.CellLink;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.library.ColorMap;
import com.emptypocketstudios.boardgame.library.ShapeRenderUtilDrawer;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class PathFindingRenderer {
    ShapeDrawer drawer;
    Rectangle viewportBounds = new Rectangle();
    float lineSize;
    TextureAtlas atlas;
    ShapeRenderUtilDrawer util;
    CellRenderer cellRenderer;

    public PathFindingRenderer(TextureAtlas atlas, ShapeDrawer drawer) {
        this.drawer = drawer;
        this.atlas = atlas;
        util = new ShapeRenderUtilDrawer();
        cellRenderer = new CellRenderer(atlas, drawer);
    }

    public void update(Rectangle viewportBounds, float lineSize) {
        this.viewportBounds.set(viewportBounds);
        this.lineSize = lineSize;
        cellRenderer.update(viewportBounds, lineSize);
    }

    public Color getCellColor(Cell c) {
        return ColorMap.getRandomColor((byte) (c.region.hashCode()));
    }

    public void debugPathFinder(Engine engine, PathFinder pathFinder) {
        if (!pathFinder.graphFinder.debug) {
            return;
        }
        for (CellLink link : pathFinder.graphFinder.usedPath) {
            debugCellPathFinding(engine, pathFinder.graphFinder.bestPaths.get(link.currentCell));
        }
    }

    public void debugCellPathFinding(Engine engine, CellLink cell) {
        if (cell == null || !viewportBounds.overlaps(cell.currentCell.boundary)) {
            return;
        }
        if (cell.currentCell.boundary.width / lineSize <= 20) {
            return;
        }
        drawer.rectangle(cell.currentCell.boundary, getCellColor(cell.currentCell));
        float textHeight = 1;

        if (cell.currentCell.boundary.width / lineSize <= 300) {
            return;
        }
        int cx = 0;
        int cy = 0;
        if (cell.originCell != null) {
            cx = cell.currentCell.cellId.x - cell.originCell.cellId.x;
            cy = cell.currentCell.cellId.y - cell.originCell.cellId.y;
        }

        float x = 0;
        if (cx == 1) {
            x = cell.currentCell.boundary.x;
        } else if (cx == 0) {
            x = cell.currentCell.boundary.x + cell.currentCell.boundary.width * 0.4f;
        } else {
            x = cell.currentCell.boundary.x + cell.currentCell.boundary.width * 0.65f;
        }

        float y = 0;
        if (cy == 1) {
            y = cell.currentCell.boundary.y;
        } else if (cy == 0) {
            y = cell.currentCell.boundary.y + cell.currentCell.boundary.height * 0.5f - 4 * textHeight;
        } else {
            y = cell.currentCell.boundary.y + cell.currentCell.boundary.height - 8 * textHeight;
        }
        float dx = textHeight;
        float dy = 1;

        util.render(drawer, "DX:" + (int) cx + " DY:" + cy,
                x + dy, y + dx, textHeight, viewportBounds);

        dx += 1.5 * textHeight;
        util.render(drawer, "W:" + (int) cell.weight,
                x + dy, y + dx, textHeight, viewportBounds);

        dx += 1.5 * textHeight;
        util.render(drawer, "T:" + (int) cell.travelWeight,
                x + dy, y + dx, textHeight, viewportBounds);

        dx += 1.5 * textHeight;
        util.render(drawer, "D:" + (int) cell.destinationWeight,
                x + dy, y + dx, textHeight, viewportBounds);
    }

    public void render(Engine engine) {
        Cell selectedCell = engine.engineControllerManager.pathfindingControls.selectedCell;
        if (selectedCell != null) {
            cellRenderer.renderCell(engine, selectedCell);
        }
        Entity selectedEntity = engine.engineControllerManager.pathfindingControls.selectedEntity;
        if (selectedEntity != null) {
            PathFollowComponent component = selectedEntity.getEntityComponent(PathFollowComponent.class);
            Array<Cell> cells =component.path;
            for (int i = component.currentIndex; i < cells.size; i++) {
                cellRenderer.renderCell(engine, cells.get(i));
            }

            for (int i = component.currentIndex; i < cells.size - 1; i++) {
                Cell c1 = cells.get(i);
                Cell c2 = cells.get(i + 1);
                cellRenderer.drawLineCells(c1, c2, Color.PURPLE, 2);
            }

            PathFinder finder = component.debugPathFinder;
            if (finder != null) {
                debugPathFinder(engine,finder);
            }
        }
    }
}
