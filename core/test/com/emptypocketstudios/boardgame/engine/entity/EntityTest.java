package com.emptypocketstudios.boardgame.engine.entity;

import com.badlogic.gdx.math.Vector2;
import com.emptypocketstudios.boardgame.TestUtil;
import com.emptypocketstudios.boardgame.engine.Engine;

import junit.framework.TestCase;

public class EntityTest extends TestCase {

    public void testTags() {
        Engine engine = TestUtil.setupTestWorld();

        Entity ent = engine.entityFactory.createEntity("Base", EntityType.HUMAN, Vector2.X, 1);
        assertFalse(ent.hasTag("Testing"));
        ent.addTag("Testing");
        assertTrue(ent.hasTag("Testing"));
    }
}