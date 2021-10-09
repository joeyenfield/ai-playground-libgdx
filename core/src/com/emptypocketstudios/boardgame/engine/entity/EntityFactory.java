package com.emptypocketstudios.boardgame.engine.entity;

import com.badlogic.gdx.math.Vector2;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.BrainComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.BTFactory;
import com.emptypocketstudios.boardgame.engine.entity.components.building.BuildingComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.building.BuildingType;
import com.emptypocketstudios.boardgame.engine.entity.components.human.HumanType;
import com.emptypocketstudios.boardgame.engine.entity.components.human.LifeComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.movement.MovementComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.movement.PathFollowComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.notifications.NotificationComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.notifications.NotificationTypes;

public class EntityFactory {
    public static final String EMPTY_HOUSE = "EMPTY_HOUSE";

    Engine engine;
    static int ENTITY_COUNTER = 0;

    public EntityFactory(Engine engine) {
        this.engine = engine;
    }

    protected Entity createEntity(String baseName, EntityType type, Vector2 pos, float cellSize) {
        Entity e = new Entity();
        e.bounds.setSize(cellSize, cellSize);
        e.name = type.name() + "_" + (ENTITY_COUNTER++) + "_" + baseName;
        e.world = engine.world;
        e.type = type;
        e.pos.set(pos.x, pos.y);
        e.update(0);
        e.addTag(type.name());
        return e;
    }

    public Entity setupHuman(String name, HumanType type, Vector2 pos, float cellSize) {
        Entity e = createEntity(name, EntityType.HUMAN, pos, cellSize);
        e.addComponent(new LifeComponent(e));
        e.addComponent(new PathFollowComponent(e));
        e.addComponent(new MovementComponent(e)).maxSpeed = e.world.engine.config.cellSize;
        e.addComponent(new NotificationComponent(e)).notification = NotificationTypes.SLEEP;
        e.addComponent(new BrainComponent(e)).setBehaviour(BTFactory.getBehaviour(type.name()));
        engine.world.addEntity(e);
        return e;
    }

    public Entity setupBuilding(String name, BuildingType type, Vector2 pos, float cellSize) {
        Entity e = createEntity(name, EntityType.BUILDING, pos, cellSize);
        e.addComponent(new NotificationComponent(e)).notification = NotificationTypes.NONE;
        e.addComponent(new BuildingComponent(e));

        BuildingComponent building = e.getEntityComponent(BuildingComponent.class);
        building.setType(type);
        if (type == BuildingType.SMALL_HOUSE) {
            e.addTag(EMPTY_HOUSE);
            e.getEntityComponent(NotificationComponent.class).notification = NotificationTypes.THINKING;
            e.getEntityComponent(NotificationComponent.class).displayTime = 999999;
        } else if (type == BuildingType.CASTLE) {
        }

        building.placeBuilding();
        engine.world.addEntity(e);

        return e;
    }
}
