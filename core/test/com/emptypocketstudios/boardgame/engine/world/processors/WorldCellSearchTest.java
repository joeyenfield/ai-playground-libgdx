package com.emptypocketstudios.boardgame.engine.world.processors;

import com.badlogic.gdx.math.Rectangle;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;
import com.emptypocketstudios.boardgame.engine.world.World;

import junit.framework.TestCase;

import org.junit.Test;

public class WorldCellSearchTest extends TestCase {

    World world;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Rectangle region = new Rectangle(0, 0, 100, 100);
        world = new World(region, 10, 10, 10, 10);
        world.loadAllChunks();
        world.worldLoadingProcessor.fill(CellType.GRASS);
    }

    @Test
    public void testSpiralSearch() {
        Cell cell = world.getCellByCellId(50, 50);
        cell.type = CellType.FOREST;

        Cell found = world.cellSearcher.spiralSearch(0, 0, 100, CellType.FOREST);
        assertNotNull(found);
        assertEquals(found, cell);
    }

    @Test
    public void testSpiralSearchOutsideRangeReturnNull() {
        Cell cell = world.getCellByCellId(50, 50);
        cell.type = CellType.FOREST;

        for(int i = 0; i < 10; i++){
            world.getCellByCellId(50+i, 50+i).type= CellType.FOREST;
        }
        Cell found = world.cellSearcher.spiralSearch(0, 0, 49, CellType.FOREST);
        assertNull(found);
    }

    @Test
    public void testRandomSearch() {
        Cell cell = world.getCellByCellId(50, 50);
        cell.type = CellType.FOREST;


        Cell found = world.cellSearcher.randomSearchScan(world.getCellByCellId(0,0), 100, CellType.FOREST);
        assertNotNull(found);
        assertEquals(found, cell);
    }

    @Test
    public void testRandomSearchOutsideRangeReturnNull() {
        Cell cell = world.getCellByCellId(50, 50);
        cell.type = CellType.FOREST;

        for(int i = 0; i < 10; i++){
            world.getCellByCellId(50+i, 50+i).type= CellType.FOREST;
        }
        Cell found = world.cellSearcher.randomSearchScan(world.getCellByCellId(0,0), 50, CellType.FOREST);
        assertNull(found);
    }

}