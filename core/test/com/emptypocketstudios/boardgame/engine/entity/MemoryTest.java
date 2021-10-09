package com.emptypocketstudios.boardgame.engine.entity;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.Memory;

import junit.framework.TestCase;

public class MemoryTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testStoreFloat() {
        Memory m = new Memory(new Entity());
        m.store("key", 1.0f);
        assertEquals(1.0, m.getFloat("key"), 0.01);
    }

}