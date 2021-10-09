package com.emptypocketstudios.boardgame.ui.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.EntityTextureAtlas;
import com.emptypocketstudios.boardgame.engine.entity.EntityType;
import com.emptypocketstudios.boardgame.engine.entity.components.human.LifeComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.notifications.NotificationComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.notifications.NotificationTypes;
import com.emptypocketstudios.boardgame.library.ShapeRenderUtilDrawer;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class EntityRenderer {
    ShapeDrawer drawer;
    Rectangle viewportBounds = new Rectangle();
    Rectangle statsBarBounds = new Rectangle();
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
        if (e.type == EntityType.HUMAN) {
            textureHuman(engine, e);
        } else if (e.type == EntityType.BUILDING) {
            textureBuilding(engine, e);
        }

        NotificationComponent notificationComponent = e.getEntityComponent(NotificationComponent.class);
        if (notificationComponent != null && notificationComponent.notification != NotificationTypes.NONE) {
            TextureAtlas.AtlasRegion emoteRegion = NotificationTypes.getTexture(notificationComponent.notification);
            if (emoteRegion != null) {
                batch.draw(emoteRegion, e.bounds.x + e.bounds.width / 4, e.bounds.y + (int) (e.bounds.height * 0.75), e.bounds.width / 2, e.bounds.height / 2);
            }
        }
    }

    public void textureBuilding(Engine engine, Entity e) {
        PolygonSpriteBatch batch = (PolygonSpriteBatch) drawer.getBatch();
        TextureAtlas.AtlasRegion[] region = EntityTextureAtlas.getBuildingTexture(e);
        batch.draw(region[0], e.bounds.x, e.bounds.y, e.bounds.width, e.bounds.height);
        if (region.length > 1) {
            batch.draw(region[1], e.bounds.x, e.bounds.y + e.bounds.height, e.bounds.width, e.bounds.height);
        }
    }

    public void textureHuman(Engine engine, Entity e) {
        PolygonSpriteBatch batch = (PolygonSpriteBatch) drawer.getBatch();

        batch.draw(EntityTextureAtlas.getHumanTexture(e), e.bounds.x, e.bounds.y, e.bounds.width, e.bounds.height);

        statsBarBounds.set(e.bounds);
        float borderSize = 0.04f;
        float borderGap = 0.01f;
        statsBarBounds.height *= borderSize;
        LifeComponent life = e.getEntityComponent(LifeComponent.class);
        if (life != null) {
            statsBarBounds.y = e.bounds.y + e.bounds.height * (1 - (borderGap + borderSize));
            statsBarBounds.width = e.bounds.width * (life.health / 100f);
            drawer.setColor(Color.RED);
            drawer.filledRectangle(statsBarBounds);

            statsBarBounds.y = e.bounds.y + e.bounds.height * (1 - 2 * (borderGap + borderSize));
            statsBarBounds.width = e.bounds.width * (life.thirst / 100f);
            drawer.setColor(Color.BLUE);
            drawer.filledRectangle(statsBarBounds);

            statsBarBounds.y = e.bounds.y + e.bounds.height * (1 - 3 * (borderGap + borderSize));
            statsBarBounds.width = e.bounds.width * (life.hunger / 100f);
            drawer.setColor(Color.BROWN);
            drawer.filledRectangle(statsBarBounds);

            statsBarBounds.y = e.bounds.y + e.bounds.height * (1 - 4 * (borderGap + borderSize));
            statsBarBounds.width = e.bounds.width * (life.energy / 100f);
            drawer.setColor(Color.PURPLE);
            drawer.filledRectangle(statsBarBounds);
        }
    }


}
