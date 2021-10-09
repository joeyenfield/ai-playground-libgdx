package com.emptypocketstudios.boardgame.engine.world;

import com.badlogic.gdx.math.Rectangle;

import junit.framework.TestCase;

public class WorldTest extends TestCase {

    World world;

    @Override
    public void setUp(){
        world = new World(new Rectangle(0,0,100,100), 3,3,5,5);
    }


    public void testUpdateLinksRegions(){
        world = new World(new Rectangle(0,0,100,100), 10,10,10,10);
        world.loadAllChunks();
        world.fillAllCells(CellType.GRASS);
        world.update(1);
        WorldChunk chunk = world.getChunkByChunkId(1,1);
    }
    public void testLoadAllChunks(){
        world = new World(new Rectangle(0,0,100,100), 3,3,5,5);
        for(int x = 0; x < 3; x++){
            for(int y = 0; y < 3; y++){
                assertNull(world.chunks[x][y]);
            }
        }
        world.loadAllChunks();
        for(int x = 0; x < 3; x++){
            for(int y = 0; y < 3; y++){
                assertNotNull(world.chunks[x][y]);
            }
        }
    }


    public void testLoadChunks(){
        world = new World(new Rectangle(0,0,100,100), 3,3,3,3);
        for(int x = 0; x < 3; x++){
            for(int y = 0; y < 3; y++){
                assertNull(world.chunks[x][y]);
            }
        }
        world.worldLoadingProcessor.loadChunk(1,1);

        //Make sure only chunk is loaded
        assertNull(world.chunks[0][0]);
        assertNull(world.chunks[0][1]);
        assertNull(world.chunks[0][2]);
        assertNull(world.chunks[1][0]);
        assertNotNull(world.chunks[1][1]);
        assertNull(world.chunks[1][2]);
        assertNull(world.chunks[2][0]);
        assertNull(world.chunks[2][1]);
        assertNull(world.chunks[2][2]);

        //Make sure Linking has worked correctly
        //Test middle cell has all connections
        WorldChunk chunk = world.chunks[1][1];
        Cell cell = chunk.cells[1][1];
        assertEquals(chunk.cells[0][0],cell.links[0][0]);
        assertEquals(chunk.cells[0][1],cell.links[0][1]);
        assertEquals(chunk.cells[0][2],cell.links[0][2]);
        assertEquals(chunk.cells[1][0],cell.links[1][0]);
        assertNull(cell.links[1][1]);
        assertEquals(chunk.cells[1][2],cell.links[1][2]);
        assertEquals(chunk.cells[2][0],cell.links[2][0]);
        assertEquals(chunk.cells[2][1],cell.links[2][1]);
        assertEquals(chunk.cells[2][2],cell.links[2][2]);

        //Make sure Linking has worked correctly
        //Test corner cell has internal links
        cell = chunk.cells[0][0];
        assertNull(cell.links[0][0]);
        assertNull(cell.links[0][1]);
        assertNull(cell.links[0][2]);
        assertNull(cell.links[1][0]);
        assertNull(cell.links[1][1]);
        assertEquals(chunk.cells[0][1], cell.links[1][2]);
        assertNull(cell.links[2][0]);
        assertEquals(chunk.cells[1][0], cell.links[2][1]);
        assertEquals(chunk.cells[1][1], cell.links[2][2]);

        world.worldLoadingProcessor.loadChunk(0,1);

        assertNull(world.chunks[0][0]);
        assertNotNull(world.chunks[0][1]);
        assertNull(world.chunks[0][2]);
        assertNull(world.chunks[1][0]);
        assertNotNull(world.chunks[1][1]);
        assertNull(world.chunks[1][2]);
        assertNull(world.chunks[2][0]);
        assertNull(world.chunks[2][1]);
        assertNull(world.chunks[2][2]);

        //Make corner Cell linked
        cell = chunk.cells[0][0];
        assertNull(cell.links[0][0]);
        assertEquals(world.getCellByCellId(2,3),cell.links[0][1]);
        assertEquals(world.getCellByCellId(2,4),cell.links[0][2]);
        assertNull(cell.links[1][0]);
        assertNull(cell.links[1][1]);
        assertEquals(chunk.cells[0][1], cell.links[1][2]);
        assertNull(cell.links[2][0]);
        assertEquals(chunk.cells[1][0], cell.links[2][1]);
        assertEquals(chunk.cells[1][1], cell.links[2][2]);

    }
}