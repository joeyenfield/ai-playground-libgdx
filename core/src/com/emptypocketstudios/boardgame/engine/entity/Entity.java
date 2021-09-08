package com.emptypocketstudios.boardgame.engine.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.entity.components.EntityComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.NotificationComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.PathFollowComponent;
import com.emptypocketstudios.boardgame.engine.messages.Message;
import com.emptypocketstudios.boardgame.engine.messages.MessageProcessor;
import com.emptypocketstudios.boardgame.engine.messages.PostOffice;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFinder;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingRequest;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingResponse;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellTypes;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.library.RectangeUtils;
import com.sun.org.apache.xalan.internal.xsltc.runtime.MessageHandler;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Entity implements Pool.Poolable, MessageProcessor<Message> {
    static int ENTITY_COUNTER = 0;

    public String name;
    public Rectangle bounds = new Rectangle();
    public int entityType = EntityType.MAN;
    Array<EntityComponent> components = new Array<>();
    HashMap<Class, EntityComponent> componentMap = new HashMap<>();
    public Vector2 pos = new Vector2();

    public World world;

    public void init(World world, short entityType) {
        if (name == null) {
            name = "ENTITY_" + CellTypes.name(entityType) + "_" + ENTITY_COUNTER++;
        }
        this.world = world;
        this.entityType = entityType;
        EntityType.updateEntityConfig(this, world.engine.cellSize);
    }

    public <T extends EntityComponent> T getEntityComponent(Class<T> classType) {
        return (T) this.componentMap.get(classType);
    }

    public void update(float delta) {
        for (int i = 0; i < components.size; i++) {
            components.get(i).update(delta);
        }

        bounds.x = pos.x - bounds.width / 2;
        bounds.y = pos.y - bounds.height / 2;
    }

    public void addComponent(EntityComponent path) {
        this.components.add(path);
        this.componentMap.put(path.getClass(), path);
    }

    public boolean overlaps(Rectangle viewportBounds) {
        return viewportBounds.overlaps(bounds);
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message instanceof PathFindingResponse) {
            PathFollowComponent component = getEntityComponent(PathFollowComponent.class);
            if (component != null) {
                component.handle((PathFindingResponse) message);
                return true;
            }
        }
        return false;
    }

    @Override
    public void reset() {
        name = null;
    }
}
