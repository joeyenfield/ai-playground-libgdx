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

    public boolean drawAllChunks = false;
    ShapeDrawer drawer;
    Rectangle viewportBounds = new Rectangle();
    float lineSize;
    TextureAtlas atlas;
    CellTextureRenderer cellRenderer;
    CellRegionRenderer cellRegionRenderer;
    EntityRenderer entityRenderer;

    public WorldChunkRenderer(TextureAtlas atlas, ShapeDrawer drawer) {
        this.drawer = drawer;
        this.atlas = atlas;
        this.cellRenderer = new CellTextureRenderer(atlas, drawer);
        this.cellRegionRenderer = new CellRegionRenderer(drawer);
        this.entityRenderer = new EntityRenderer(atlas, drawer);
    }

    public void update(Rectangle viewportBounds, float lineSize) {
        this.viewportBounds.set(viewportBounds);
        this.lineSize = lineSize;
        this.cellRenderer.update(viewportBounds, lineSize);
        this.entityRenderer.update(viewportBounds, lineSize);
        this.cellRegionRenderer.update(viewportBounds, lineSize);
    }


    public void renderWorldChunkCells(Engine engine, WorldChunk chunk) {
        if (chunk == null || !viewportBounds.overlaps(chunk.boundary)) {
            return;
        }

        if (drawAllChunks) {
            drawer.filledRectangle(chunk.boundary, Color.GREEN);
        }

        for (int cellX = 0; cellX < chunk.numCellsX; cellX++) {
            for (int cellY = 0; cellY < chunk.numCellsY; cellY++) {
                cellRenderer.textureCell(engine, chunk.getCellByIndex(cellX, cellY));

//                cellRenderer.renderDebugCell(engine, chunk.getCellByIndex(cellX, cellY));
                cellRegionRenderer.renderCellRegionBoundary(engine, chunk.getCellByIndex(cellX, cellY));
            }
        }

        if (drawAllChunks) {
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
