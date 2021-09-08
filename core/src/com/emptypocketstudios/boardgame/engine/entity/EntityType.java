package com.emptypocketstudios.boardgame.engine.entity;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.emptypocketstudios.boardgame.engine.entity.components.MovementComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.NotificationComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.PathFollowComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.WanderComponent;
import com.emptypocketstudios.boardgame.engine.world.Cell;

import java.util.HashMap;

public class EntityType {
    public static HashMap<Integer, TextureAtlas.AtlasRegion> textures = new HashMap<>();

    public static final short NONE = 0;
    public static final short MAN = 10;
    public static final short TREE = 20;
    public static final short TREE_EMPTY = 20;


    public static TextureAtlas.AtlasRegion getTexture(Entity e) {

        if (textures.containsKey(e.entityType)) {
            return textures.get(e.entityType);
        } else {
            return textures.get(NONE);
        }
    }

    public static void updateEntityConfig(Entity e, float cellSize) {
        if (e.entityType == MAN) {
            e.bounds.setSize(cellSize, cellSize);
            e.addComponent(new MovementComponent(e));
            e.addComponent(new PathFollowComponent(e));
            e.addComponent(new NotificationComponent(e));
            e.addComponent(new WanderComponent(e));

            e.getEntityComponent(NotificationComponent.class).notification = NotificationTypes.SLEEP;
            e.getEntityComponent(WanderComponent.class).stableTime = 5 + MathUtils.random(60);
            e.getEntityComponent(MovementComponent.class).maxSpeed = 30;
        }
    }

    public static void setupAtlas(TextureAtlas atlas) {
        textures.put(+NONE, atlas.findRegion("Environment/medievalEnvironment", 20));
        textures.put(+MAN, atlas.findRegion("Unit/medievalUnit", 1));
        textures.put(+TREE, atlas.findRegion("Environment/medievalEnvironment", 3));
    }
}
