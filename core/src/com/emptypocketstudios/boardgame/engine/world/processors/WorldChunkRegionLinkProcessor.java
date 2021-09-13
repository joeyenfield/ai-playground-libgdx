package com.emptypocketstudios.boardgame.engine.world.processors;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellTypes;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;
import com.emptypocketstudios.boardgame.engine.world.WorldChunkRegionNodeLink;

public class WorldChunkRegionLinkProcessor extends WorldChunkProcessor {
    public static final int RESET_LAYER_ID = -1000;

    public WorldChunkRegionLinkProcessor(WorldChunk chunk) {
        super(chunk);
    }

    Pool<WorldChunkRegionNodeLink> linkPool = new ReflectionPool<WorldChunkRegionNodeLink>(WorldChunkRegionNodeLink.class);

    @Override
    protected void run() {
        updateRegionLinks();
    }

    protected void addRegionLink(Cell a, Cell b, Array<WorldChunkRegionNodeLink> links) {
        if (a != null
                && b != null
                && a.region.regionId != WorldChunkRegionProcessor.RESET_LAYER_ID
                && b.region.regionId != WorldChunkRegionProcessor.RESET_LAYER_ID
        ) {
            WorldChunkRegionNodeLink link = linkPool.obtain();
            link.source.set(a);
            link.current.set(b);
            if (!links.contains(link, false)) {
                links.add(link);
            } else {
                linkPool.free(link);
            }
        }
    }

    protected void updateRegionLinks() {
        //Free all existing links
        linkPool.freeAll(chunk.regionNodeDiagonalLinks);
        linkPool.freeAll(chunk.regionNodeLinks);

        Cell start;
        int dx = 0;
        int dy = 0;
        for (int x = 0; x < chunk.numCellsX; x++) {
            //Up
            start = chunk.cells[x][0];
            dx = 0;
            dy = -1;
            if (start.canMove(dx, dy)) {
                addRegionLink(start, start.getLink(dx, dy), chunk.regionNodeLinks);
            }

            //Down
            start = chunk.cells[x][chunk.numCellsY - 1];
            dx = 0;
            dy = 1;
            if (start.canMove(dx, dy)) {
                addRegionLink(start, start.getLink(dx, dy), chunk.regionNodeLinks);
            }
        }

        for (int y = 0; y < chunk.numCellsY; y++) {
            //Left
            start = chunk.cells[0][y];
            dx = -1;
            dy = 0;
            if (start.canMove(dx, dy)) {
                addRegionLink(start, start.getLink(dx, dy), chunk.regionNodeLinks);
            }

            //Right
            start = chunk.cells[chunk.numCellsX - 1][y];
            dx = 1;
            dy = 0;
            if (start.canMove(dx, dy)) {
                addRegionLink(start, start.getLink(dx, dy), chunk.regionNodeLinks);
            }
        }

        //Down Left
        start = chunk.cells[0][0];
        dx = -1;
        dy = -1;
        if (start.canMove(dx, dy)) {
            addRegionLink(start, start.getLink(dx, dy), chunk.regionNodeDiagonalLinks);
        }

        //Up Right
        start = chunk.cells[chunk.numCellsX - 1][chunk.numCellsY - 1];
        dx = 1;
        dy = 1;
        if (start.canMove(dx, dy)) {
            addRegionLink(start, start.getLink(dx, dy), chunk.regionNodeDiagonalLinks);
        }

        //Up Left
        start = chunk.cells[0][chunk.numCellsY - 1];
        dx = -1;
        dy = 1;
        if (start.canMove(dx, dy)) {
            addRegionLink(start, start.getLink(dx, dy), chunk.regionNodeDiagonalLinks);
        }

        //Down Right
        start = chunk.cells[chunk.numCellsX - 1][0];
        dx = 1;
        dy = -1;
        if (start.canMove(dx, dy)) {
            addRegionLink(start, start.getLink(dx, dy), chunk.regionNodeDiagonalLinks);
        }
    }

}
