package com.emptypocketstudios.boardgame.ui.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.EntityType;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class WorldChunkRenderer {

    public boolean drawChunkBoundary = true;
    public boolean drawCellBoundary = true;
    public boolean drawCellDebugText = true;
    public boolean drawCellTexture = true;
    public boolean drawCellRegions = true;

    public CellTextureRenderer cellTextureRenderer;
    public CellDebugRenderer cellDebugRenderer;

    ShapeDrawer drawer;
    Rectangle viewportBounds = new Rectangle();
    float lineSize;
    TextureAtlas atlas;
    EntityRenderer entityRenderer;

    public WorldChunkRenderer(TextureAtlas atlas, ShapeDrawer drawer) {
        this.drawer = drawer;
        this.atlas = atlas;
        this.cellTextureRenderer = new CellTextureRenderer(atlas, drawer);
        this.cellDebugRenderer = new CellDebugRenderer(drawer);
        this.entityRenderer = new EntityRenderer(atlas, drawer);
    }

    public void update(Rectangle viewportBounds, float lineSize) {
        this.viewportBounds.set(viewportBounds);
        this.lineSize = lineSize;
        this.cellTextureRenderer.update(viewportBounds, lineSize);
        this.cellDebugRenderer.update(viewportBounds, lineSize);
        this.entityRenderer.update(viewportBounds, lineSize);
    }


    public void renderWorldChunkCells(Engine engine, WorldChunk chunk) {
        if (chunk == null || !viewportBounds.overlaps(chunk.boundary)) {
            return;
        }

        if (drawChunkBoundary) {
            drawer.filledRectangle(chunk.boundary, Color.GREEN);
        }

        for (int cellX = 0; cellX < chunk.numCellsX; cellX++) {
            for (int cellY = 0; cellY < chunk.numCellsY; cellY++) {

                if (drawCellTexture) {
                    cellTextureRenderer.drawCell(engine, chunk.getCellByIndex(cellX, cellY));
                }
                if (drawCellBoundary) {
                    cellDebugRenderer.drawCellBoundary(engine, chunk.getCellByIndex(cellX, cellY));
                }
                if (drawCellRegions) {
                    cellDebugRenderer.drawRegionDebug(engine, chunk.getCellByIndex(cellX, cellY));
                }
                if(drawCellDebugText){
                    cellDebugRenderer.drawCellText(engine, chunk.getCellByIndex(cellX, cellY));
                }
            }
        }

        if (drawChunkBoundary) {
            drawer.rectangle(chunk.boundary, Color.BLACK, lineSize * 2);
        }
    }

    public void renderWorldChunkEntities(Engine engine, WorldChunk chunk, EntityType type) {
        if (chunk == null || !viewportBounds.overlaps(chunk.boundary)) {
            return;
        }
        for (int i = 0; i < chunk.entities.size; i++) {
            Entity entity = chunk.entities.get(i);
            if (type == null || entity.type == type) {
                if (entity.overlaps(viewportBounds)) {
                    entityRenderer.textureEntity(engine, entity);
                    if (entity == engine.engineControllerManager.pathfindingControls.selectedEntity) {
                        entityRenderer.debugEntity(engine, entity);
                    }
                }
            }
        }
    }
}
