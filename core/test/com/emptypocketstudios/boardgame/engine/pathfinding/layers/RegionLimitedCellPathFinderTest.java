package com.emptypocketstudios.boardgame.engine.pathfinding.layers;

import com.badlogic.gdx.math.Rectangle;
import com.emptypocketstudios.boardgame.engine.pathfinding.cells.CellLink;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellTypes;
import com.emptypocketstudios.boardgame.engine.world.World;

import junit.framework.TestCase;

public class RegionLimitedCellPathFinderTest extends TestCase {

    World world;
    RegionLimitedCellPathFinder finder;

    public void setUp() throws Exception {
        super.setUp();

        finder = new RegionLimitedCellPathFinder();
        world = new World(new Rectangle(0, 0, 100, 100), 2, 2, 10, 10);
        world.loadAllChunks();
        world.fillAllCells(CellTypes.GRASS);
        world.update(1);
    }

    public void testSort() {
        CellLink linkA = new CellLink();
        CellLink linkB = new CellLink();
        CellLink linkC = new CellLink();

        linkA.weight = 10;
        linkB.weight = 5;
        linkC.weight = 20;
        finder.openPaths.add(linkA);
        finder.openPaths.add(linkB);
        finder.openPaths.add(linkC);

        assertEquals(3, finder.openPaths.size());
        assertEquals(5, finder.openPaths.poll().weight);
        assertEquals(10, finder.openPaths.poll().weight);
        assertEquals(15, finder.openPaths.poll().weight);
    }
}