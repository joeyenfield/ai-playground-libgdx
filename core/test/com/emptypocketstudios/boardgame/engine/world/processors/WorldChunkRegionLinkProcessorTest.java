package com.emptypocketstudios.boardgame.engine.world.processors;

import com.badlogic.gdx.math.Rectangle;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;

import junit.framework.TestCase;

public class WorldChunkRegionLinkProcessorTest extends TestCase {

    World world;

    @Override
    public void setUp(){
        world = new World(new Rectangle(0,0,100,100), 3,3,5,5);
        world.loadAllChunks();
    }

    public void testProccessingInWorldSetup() {
        WorldChunk chunk = world.getChunkByChunkId(1,1);

        WorldChunkRegionLinkProcessor processor = new WorldChunkRegionLinkProcessor(chunk);
        processor.run();
        assertEquals(4,chunk.regionNodeDiagonalLinks.size);
        assertEquals(4,chunk.regionNodeLinks.size);
    }

}