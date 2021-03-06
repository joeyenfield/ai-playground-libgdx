package com.emptypocketstudios.boardgame.library;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class IntersectorUtils {

    static Vector2 p12 = new Vector2();
    static Vector2 p1t = new Vector2();

    public static float getParametricPosition(Vector2 p1, Vector2 p2, Vector2 pt) {
        float length = p12.set(p2).sub(p1).len();
        p12.scl(1 / length);
        p1t.set(pt).sub(p1);
        return p12.dot(p1t) / length;
    }

    /**
     * Gets the parametrics position of a line segment along a line
     *
     * @param p1
     * @param p2
     * @param p
     * @return
     */
    public static void projectPointOntoLineSegment(Vector2 p1, Vector2 p2, Vector2 p, Vector2 out) {
        float px = p2.x - p1.x;
        float py = p2.y - p1.y;
        float dAB = px * px + py * py;
        float u = ((p.x - p1.x) * px + (p.y - p1.y) * py) / dAB;
        out.x = p1.x + u * px;
        out.y = p1.y + u * py;
    }

    public static boolean intersects(Rectangle bounds, Vector2 c, float radius) {
        float closestX = c.x;
        float closestY = c.y;
        if (c.x < bounds.x) {
            closestX = bounds.x;
        } else if (c.x > bounds.x + bounds.width) {
            closestX = bounds.x + bounds.width;
        }

        if (c.y < bounds.y) {
            closestY = bounds.y;
        } else if (c.y > bounds.y + bounds.height) {
            closestY = bounds.y + bounds.height;
        }

        closestX = closestX - c.x;
        closestX *= closestX;
        closestY = closestY - c.y;
        closestY *= closestY;

        return closestX + closestY < radius * radius;
    }

    public static boolean intersects(Rectangle r1, Rectangle r2) {
        System.out.println("R1 : " + r1.overlaps(r2));
        System.out.println("R2 : " + r2.overlaps(r1));
        return false;
    }

    public static void main(String[] args) {
        Rectangle r1 = new Rectangle(-430.99982f, 5.0f, 1280.0f, 720.0001f);
        Rectangle r2 = new Rectangle(0.0f, -200.0f, 200.0f, 200.0f);

        intersects(r1, r2);
    }
}
