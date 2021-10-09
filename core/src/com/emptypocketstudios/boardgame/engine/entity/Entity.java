package com.emptypocketstudios.boardgame.engine.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.messages.Message;
import com.emptypocketstudios.boardgame.engine.messages.MessageProcessor;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;

import java.util.HashMap;
import java.util.Objects;

public class Entity implements Pool.Poolable, MessageProcessor<Message> {
    public String name;
    public EntityType type = EntityType.HUMAN;
    public Rectangle bounds = new Rectangle();
    public Vector2 pos = new Vector2();

    Array<EntityComponent> components = new Array<>();
    ArrayMap<Class, EntityComponent> componentMap = new ArrayMap<>();
    private Array<String> tags = new Array<>();

    public World world;

    public boolean hasTag(String tag) {
        return tags.contains(tag, false);
    }

    public Array<String> getTags() {
        return tags;
    }

    public void addTag(String tag) {
        if (!tags.contains(tag, false)) {
            tags.add(tag);
            WorldChunk chunk = getCurrentChunk();
            if (chunk != null) {
                chunk.addEntityTags(tag, this);
            }
        }
    }

    public void removeTag(String tag) {
        if (tags.contains(tag, false)) {
            tags.removeValue(tag, false);
            WorldChunk chunk = getCurrentChunk();
            if (chunk != null) {
                chunk.removeEntityTags(tag, this);
            }
        }
    }

    public <T extends EntityComponent> T getEntityComponent(Class<T> classType) {
        return (T) this.componentMap.get(classType);
    }

    public <T extends EntityComponent> T addComponent(T path) {
        this.components.add(path);
        this.componentMap.put(path.getClass(), path);
        return path;
    }

    public void update(float delta) {
        for (int i = 0; i < components.size; i++) {
            components.get(i).update(delta);
        }
        bounds.x = pos.x - bounds.width / 2;
        bounds.y = pos.y - bounds.height / 2;
    }

    public boolean overlaps(Rectangle viewportBounds) {
        return bounds.overlaps(viewportBounds) || viewportBounds.overlaps(bounds);
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    @Override
    public void reset() {
        name = null;
        Pools.freeAll(components);
        components.clear();
        tags.clear();
        world = null;
        type = null;
    }

    public Cell getCurrentCell() {
        return world.getCellAtWorldPosition(pos);
    }

    public WorldChunk getCurrentChunk() {
        return world.getChunkAtWorldPosition(pos);
    }

    public void dumpTags() {
        world.engine.log(" DumpTags", this, "TAGS[" + tags.toString(",") + "]");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(name, entity.name) && type == entity.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
