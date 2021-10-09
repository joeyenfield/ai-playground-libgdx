package com.emptypocketstudios.boardgame.ui.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.entity.EntityType;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class WorldRenderer {
    public boolean drawWorldBounds = true;
    ShapeDrawer drawer;

    WorldChunkRenderer worldChunkRenderer;

    TextureAtlas atlas;
    Rectangle viewportBounds = new Rectangle();
    float lineSize;

    public WorldRenderer(TextureAtlas atlas, ShapeDrawer drawer) {
        this.drawer = drawer;
        this.atlas = atlas;
        worldChunkRenderer = new WorldChunkRenderer(atlas,drawer);
    }

    public void update(Rectangle viewportBounds, float lineSize) {
        this.viewportBounds.set(viewportBounds);
        this.lineSize = lineSize;
        worldChunkRenderer.update(viewportBounds, lineSize);
    }


    public void renderWorld(Engine engine, World world) {
        for (int chunkX = 0; chunkX < world.chunksX; chunkX++) {
            for (int chunkY = 0; chunkY < world.chunksY; chunkY++) {
                WorldChunk chunk = world.getChunkByChunkId(chunkX, chunkY);
                worldChunkRenderer.renderWorldChunkCells(engine, chunk);
            }
        }
        if (drawWorldBounds) {
            drawer.rectangle(world.boundary, Color.PURPLE, lineSize);
        }

        // Render People
        for (int chunkX = 0; chunkX < world.chunksX; chunkX++) {
            for (int chunkY = 0; chunkY < world.chunksY; chunkY++) {
                WorldChunk chunk = world.getChunkByChunkId(chunkX, chunkY);
                worldChunkRenderer.renderWorldChunkEntities(engine, chunk, EntityType.HUMAN);
            }
        }

        // Render Buildings
        for (int chunkX = 0; chunkX < world.chunksX; chunkX++) {
            for (int chunkY = 0; chunkY < world.chunksY; chunkY++) {
                WorldChunk chunk = world.getChunkByChunkId(chunkX, chunkY);
                worldChunkRenderer.renderWorldChunkEntities(engine, chunk, EntityType.BUILDING);
            }
        }
    }
}
