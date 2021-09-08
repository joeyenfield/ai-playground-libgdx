package com.emptypocketstudios.boardgame.engine.world.processors;

import com.emptypocketstudios.boardgame.engine.world.WorldChunk;

public abstract class WorldChunkProcessor {
    public long processTime = 0;
    WorldChunk chunk;
    public WorldChunkProcessor(WorldChunk chunk) {
        this.chunk = chunk;
    }


    public void process() {
        long start = System.currentTimeMillis();
        run();
        processTime = System.currentTimeMillis()-start;
    }

    protected abstract void run();
}
