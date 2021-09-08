package com.emptypocketstudios.boardgame.ui.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.EntityType;
import com.emptypocketstudios.boardgame.engine.entity.NotificationTypes;
import com.emptypocketstudios.boardgame.engine.entity.components.NotificationComponent;
import com.emptypocketstudios.boardgame.engine.pathfinding.cells.CellLink;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellTypes;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;
import com.emptypocketstudios.boardgame.library.ColorMap;
import com.emptypocketstudios.boardgame.library.ShapeRenderUtilDrawer;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class EntityRenderer {
    ShapeDrawer drawer;
    Rectangle viewportBounds = new Rectangle();
    float lineSize;
    TextureAtlas atlas;
    ShapeRenderUtilDrawer util;

    public EntityRenderer(TextureAtlas atlas, ShapeDrawer drawer) {
        this.drawer = drawer;
        this.atlas = atlas;
        util = new ShapeRenderUtilDrawer();
    }

    public void update(Rectangle viewportBounds, float lineSize) {
        this.viewportBounds.set(viewportBounds);
        this.lineSize = lineSize;
    }

    public void debugEntity(Engine engine, Entity e) {
        drawer.setColor(Color.BLACK);
        drawer.rectangle(e.bounds);
        drawer.setColor(Color.RED);
        drawer.filledCircle(e.pos, 3f);
    }

    public void textureEntity(Engine engine, Entity e) {
        PolygonSpriteBatch batch = (PolygonSpriteBatch) drawer.getBatch();
        TextureAtlas.AtlasRegion region = EntityType.getTexture(e);
        batch.draw(region, e.bounds.x, e.bounds.y, e.bounds.width, e.bounds.height);

        NotificationComponent notificationComponent = e.getEntityComponent(NotificationComponent.class);
        if (notificationComponent != null && notificationComponent.notification != NotificationTypes.NONE) {
            TextureAtlas.AtlasRegion emoteRegion = NotificationTypes.getTexture(notificationComponent.notification);
            if(emoteRegion != null) {
                batch.draw(emoteRegion, e.bounds.x + e.bounds.width / 4, e.bounds.y + (int) (e.bounds.height * 0.75), e.bounds.width / 2, e.bounds.height / 2);
            }
        }
    }

}
