package com.emptypocketstudios.boardgame.library;

import com.badlogic.gdx.math.Vector2;

import junit.framework.TestCase;

import org.junit.Test;

public class IntersectorUtilsTest extends TestCase {

    @Test
    public void testPerpendicular() {
        Vector2 p1 = new Vector2(10, 10);
        Vector2 p2 = new Vector2(10, 12);
        Vector2 p = new Vector2(11, 11);
        Vector2 out = new Vector2(0, 0);

        IntersectorUtils.projectPointOntoLineSegment(p1, p2, p, out);
        assertEquals(10, out.x, 0.1f);
        assertEquals(11, out.y, 0.1f);
    }

    @Test
    public void testParametric() {
        Vector2 p1 = new Vector2(0, 0);
        Vector2 p2 = new Vector2(0, 10);
        Vector2 p = new Vector2(5f, 5f);

        p.set(5, 5);
        float r = IntersectorUtils.getParametricPosition(p1, p2, p);
        assertEquals(0.5, r, 0.1);

        p.set(0, 0);
        r = IntersectorUtils.getParametricPosition(p1, p2, p);
        assertEquals(0, r, 0.1);


        p.set(10000, 0);
        r = IntersectorUtils.getParametricPosition(p1, p2, p);
        assertEquals(0, r, 0.1);


        p.set(0, -5);
        r = IntersectorUtils.getParametricPosition(p1, p2, p);
        assertEquals(-0.5, r, 0.1);
    }
}