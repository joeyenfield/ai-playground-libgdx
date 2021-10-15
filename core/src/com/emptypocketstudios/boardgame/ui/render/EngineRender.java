package com.emptypocketstudios.boardgame.ui.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.entity.EntityTextureAtlas;
import com.emptypocketstudios.boardgame.engine.entity.components.notifications.NotificationTypes;
import com.emptypocketstudios.boardgame.engine.pathfinding.cells.CellLink;
import com.emptypocketstudios.boardgame.engine.pathfinding.cells.CellPathFinder;
import com.emptypocketstudios.boardgame.library.CameraHelper;
import com.emptypocketstudios.boardgame.library.GraphicsToolkit;
import com.emptypocketstudios.boardgame.library.ShapeRenderUtilDrawer;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class EngineRender {
    public boolean draw2DAxis = true;

    public WorldRenderer worldRenderer;
    public PathFindingRenderer pathRenderer;

    TextureAtlas atlas;
    Texture texture;
    TextureRegion region;
    PolygonSpriteBatch batch;
    ShapeDrawer drawer;
    ShapeRenderUtilDrawer util;
    CameraHelper helper;
    float lineSize;
    Rectangle viewportBounds = new Rectangle();

    public EngineRender(TextureAtlas atlas) {
        EntityTextureAtlas.setupAtlas(atlas);
        NotificationTypes.setupAtlas(atlas);

        batch = new PolygonSpriteBatch();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        texture = new Texture(pixmap); //remember to dispose of later
        pixmap.dispose();
        region = new TextureRegion(texture, 0, 0, 1, 1);
        drawer = new ShapeDrawer(batch, region);
        util = new ShapeRenderUtilDrawer();
        helper = new CameraHelper();
        this.atlas = atlas;
        worldRenderer = new WorldRenderer(atlas, drawer);
        pathRenderer = new PathFindingRenderer(atlas, drawer);
    }

    public void init(Viewport viewport) {
        helper.getBounds(viewport, viewportBounds);
        lineSize = helper.getScreenToCameraPixelX(viewport.getCamera(), 1);
        worldRenderer.update(viewportBounds, lineSize);
        pathRenderer.update(viewportBounds, lineSize);
        batch.setProjectionMatrix(viewport.getCamera().combined);
    }

    public void render(Viewport viewport, Engine engine) {
        init(viewport);
        batch.enableBlending();
        batch.begin();

        //Draw 2D Axis background
        renderBackground(viewport);
        renderEngine(engine);
        renderOverlay(viewport, engine);
        renderPathFinding(viewport, engine);

        batch.end();
    }

    private void renderPathFinding(Viewport viewport, Engine engine) {
        pathRenderer.render(engine);
    }

    private void renderBackground(Viewport viewport) {
        if (draw2DAxis) {
            GraphicsToolkit.draw2DAxis(drawer, (OrthographicCamera) viewport.getCamera(), 100, Color.GRAY);
        }
    }

    private void renderOverlay(Viewport viewport, Engine engine) {
    }


    private void renderEngine(Engine engine) {
        worldRenderer.renderWorld(engine, engine.world);
    }

    private void drawPath(Viewport viewport, Engine engine, CellPathFinder graph, CellLink p, Color c1, Color c2, float lineThick) {
        drawer.setDefaultLineWidth(lineSize * lineThick);
        if (p != null && p.originCell != null && p.currentCell != null) {
            drawer.line(p.originCell.pos, p.currentCell.pos, c1, c2);
        }
        drawer.setDefaultLineWidth(1);
    }

    public void dispose() {
        texture.dispose();
    }
}
