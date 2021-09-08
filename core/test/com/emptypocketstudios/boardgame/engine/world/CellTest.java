package com.emptypocketstudios.boardgame.engine.world;

import junit.framework.TestCase;

public class CellTest extends TestCase {

    Cell cell;

    @Override
    public void setUp() {
        cell = new Cell();
        cell.type = CellTypes.GRASS;

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (!(x == 1 && y == 1)) {
                    cell.links[x][y] = new Cell();
                    cell.links[x][y].type = CellTypes.GRASS;
                }
            }
        }
    }

    public void testCanMove() {
        assertTrue(cell.canMove(1, 0));
        assertTrue(cell.canMove(0, 1));
        assertTrue(cell.canMove(-1, 0));
        assertTrue(cell.canMove(0, -1));

        assertTrue(cell.canMove(1, 1));
        assertTrue(cell.canMove(1, -1));
        assertTrue(cell.canMove(-1, 1));
        assertTrue(cell.canMove(-1, -1));
    }


    public void testCanMoveDiagonals() {
        cell.unlink(1, 0);
        cell.unlink(-1, 0);
        cell.unlink(0, 1);
        cell.unlink(0, -1);
        assertFalse(cell.canMove(1, 0));
        assertFalse(cell.canMove(0, 1));
        assertFalse(cell.canMove(-1, 0));
        assertFalse(cell.canMove(0, -1));

        assertFalse(cell.canMove(1, 1));
        assertFalse(cell.canMove(1, -1));
        assertFalse(cell.canMove(-1, 1));
        assertFalse(cell.canMove(-1, -1));
    }

    public void testCannotMoveDiagonalsUpRightBlocked() {
        cell.unlink(1, 0);
        cell.unlink(0, 1);
        assertFalse(cell.canMove(1, 0));
        assertFalse(cell.canMove(0, 1));
        assertTrue(cell.canMove(-1, 0));
        assertTrue(cell.canMove(0, -1));

        assertFalse(cell.canMove(1, 1));
        assertTrue(cell.canMove(1, -1));
        assertTrue(cell.canMove(-1, 1));
        assertTrue(cell.canMove(-1, -1));
    }
}