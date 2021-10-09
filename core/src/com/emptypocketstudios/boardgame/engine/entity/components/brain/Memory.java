package com.emptypocketstudios.boardgame.engine.entity.components.brain;

import com.emptypocketstudios.boardgame.engine.entity.Entity;

import java.util.HashMap;

public class Memory {
    Entity entity;
    HashMap<String, Object> memory = new HashMap<>();

    public Memory(Entity entity) {
        this.entity = entity;
    }

    public void store(String key, Object data) {
        memory.put(key, data);
    }

    public void storeInt(String key, int data) {
        memory.put(key, data);
    }

    public void storeFloat(String key, float data) {
        memory.put(key, data);
    }

    public void storeFloat(String key, Float data) {
        memory.put(key, data);
    }

    public Object getObject(String key) {
        return memory.get(key);
    }

    public String getString(String key) {
        return (String) memory.get(key);
    }

    public float getFloat(String key) {
        Object data = memory.get(key);
        return (float) data;
    }

    public int getInt(String key) {
        Object data = memory.get(key);
        return (int) data;
    }

    public void clear() {
        memory.clear();
    }

    public void forget(String key) {
        memory.remove(key);
    }

    public void braindump() {
        for (String key : memory.keySet()) {
            entity.world.engine.log(" MemoryDump", entity, "  " + key + " : " + memory.get(key));
        }
    }

    public boolean hasMemory(String memoryName) {
        return memory.containsKey(memoryName);
    }
}
