package com.emptypocketstudios.boardgame.engine.entity.components.resource;

import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.EntityComponent;

import java.util.HashMap;

public class ResourceComponent extends EntityComponent {

    HashMap<Resource, ResourceStore> storage = new HashMap<>();

    public ResourceComponent(Entity entity) {
        super(entity);
    }

    public void enableStorage(Resource resource, int capacity) {
        storage.put(resource, new ResourceStore().set(resource, capacity, 0));
    }

    public boolean canStore(Resource type) {
        return storage.containsKey(type);
    }

    public boolean hasCapacity(Resource type, int quantity) {
        ResourceStore store = storage.get(type);
        if (store != null && store.current + quantity < store.capacity) {
            return true;
        }
        return false;
    }

    public int getQuantity(Resource type) {
        ResourceStore store = storage.get(type);
        if (store != null) {
            return store.current;
        }
        return 0;
    }

    public boolean store(Resource type, int quantity) {
        ResourceStore store = storage.get(type);
        if (store != null && store.current + quantity < store.capacity) {
            store.current += quantity;
            return true;
        }
        return false;
    }

    public boolean remove(Resource type, int quantity) {
        ResourceStore store = storage.get(type);
        if (store != null && storage.get(type).current - quantity >= 0) {
            storage.get(type).current -= quantity;
            return true;
        }
        return false;
    }

    @Override
    public void update(float delta) {

    }
}
