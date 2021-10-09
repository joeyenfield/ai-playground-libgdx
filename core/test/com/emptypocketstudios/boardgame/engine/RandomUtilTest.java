package com.emptypocketstudios.boardgame.engine;

import junit.framework.TestCase;

public class RandomUtilTest extends TestCase {

    public void testRandom() {
        RandomUtil util = new RandomUtil(0);
        assertEquals(72, util.random(100));
        assertEquals(60, util.random(100));
        assertEquals(52, util.random(100));
        assertEquals(92, util.random(100));
        assertEquals(31, util.random(100));

        util.setSeed(0);
        assertEquals(72, util.random(100));
        assertEquals(60, util.random(100));
        assertEquals(52, util.random(100));
        assertEquals(92, util.random(100));
        assertEquals(31, util.random(100));
    }

}