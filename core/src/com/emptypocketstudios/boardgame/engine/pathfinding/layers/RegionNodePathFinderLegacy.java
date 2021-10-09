package com.emptypocketstudios.boardgame.engine.pathfinding.layers;

import com.badlogic.gdx.ai.msg.PriorityQueue;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFindingResultEnum;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;
import com.emptypocketstudios.boardgame.engine.world.processors.WorldChunkRegionProcessor;

import java.util.HashMap;

public class RegionNodePathFinderLegacy extends RegionNodePathFinder{
    long start = 0;
    long limit = 30;
    Array<RegionLinks> createdRegionLinks = new Array<RegionLinks>();
    PriorityQueue<RegionLinks> openRegions = new PriorityQueue<>();
    ArrayMap<RegionNode, RegionLinks> bestPath = new ArrayMap<>();
    boolean diagonal = false;

    public PathFindingResultEnum search(World world, Cell startCell, Cell targetCell, Array<RegionNode> path,
                                        long timeLimit, boolean diagonal) {
        PathFindingResultEnum result;

        this.limit = timeLimit;
        this.diagonal = diagonal;

        start = System.currentTimeMillis();
        RegionNode startRegionNode = Pools.obtain(RegionNode.class);
        RegionNode targetRegionNode = Pools.obtain(RegionNode.class);

        startRegionNode.set(startCell);
        targetRegionNode.set(targetCell);

        if (startRegionNode.equals(targetRegionNode)) {
            path.add(startRegionNode);
            result = PathFindingResultEnum.SUCCESS;
        } else {
            result = findPath(world, startRegionNode, targetRegionNode, path);
        }

        Pools.freeAll(createdRegionLinks);
        Pools.free(startRegionNode);
        Pools.free(targetRegionNode);

        bestPath.clear();
        openRegions.clear();
        return result;
    }

    public boolean reachedLimit() {
        return System.currentTimeMillis() - start > limit;
    }

    protected PathFindingResultEnum findPath(World world, RegionNode startRegionNode, RegionNode targetRegionNode, Array<RegionNode> path) {
        RegionLinks start = createRegion();
        start.source = null;
        start.current.set(startRegionNode);
        start.weight = 0;
        start.travelWeight = 0;
        openRegions.add(start);

        Array<RegionLinks> chunkLinks = new Array<>();
        while (openRegions.size() > 0) {
            if (reachedLimit()) {
                return PathFindingResultEnum.TIMEOUT;
            }

            RegionLinks currentLink = openRegions.poll();

            //Reached Target
            if (currentLink.current.equals(targetRegionNode)) {
                workOutPath(currentLink, path);
                return PathFindingResultEnum.SUCCESS;
            }

            WorldChunk chunk = world.getChunkByChunkId(currentLink.current.chunkId);
            getLinks(chunk, currentLink, chunkLinks);

            for (int i = 0; i < chunkLinks.size; i++) {
                RegionLinks nextLink = chunkLinks.get(i);
                nextLink.parent = currentLink;
                workOutWeight(currentLink, nextLink, targetRegionNode, currentLink.weight);
                if (bestPath.containsKey(nextLink.current)) {
                    //Get previous
                    RegionLinks bestLink = bestPath.get(nextLink.current);
                    if (bestLink.weight > nextLink.weight) {
                        bestPath.put(nextLink.current, nextLink);
                        openRegions.add(nextLink);
                    }
                } else {
                    bestPath.put(nextLink.current, nextLink);
                    openRegions.add(nextLink);
                }
            }
            chunkLinks.clear();
        }
        return PathFindingResultEnum.NOT_FOUND;
    }

    protected void workOutPath(RegionLinks regionLink, Array<RegionNode> path) {
        while (regionLink != null) {
            RegionNode crumb = Pools.obtain(RegionNode.class);
            crumb.set(regionLink.current);
            path.add(crumb);
            regionLink = regionLink.parent;
            if (reachedLimit()) {
                System.out.println("Reached Time Limit - trackback");
                path.clear();
                return;
            }
        }
    }

    protected void workOutWeight(RegionLinks parent, RegionLinks current, RegionNode target, float weight) {
        current.travelWeight = parent.travelWeight + 1;
//        Chebosky
        current.destWeight = Math.min(
                Math.abs(target.chunkId.x - current.current.chunkId.x),
                Math.abs(target.chunkId.y - current.current.chunkId.y)
        );
//        current.destWeight = target.dst2(current.current);

        current.weight = current.travelWeight + current.destWeight;
    }

    protected void getLinks(WorldChunk chunk, RegionLinks parent, Array<RegionLinks> links) {
        Cell start;
        Cell end;
        int dx = 0;
        int dy = 0;
        for (int x = 0; x < chunk.numCellsX; x++) {
            //Up
            start = chunk.cells[x][0];
            dx = 0;
            dy = -1;
            if (start.region.regionId == parent.current.regionId && start.canMove(dx, dy)) {
                addRegionLink(start, start.getLink(dx, dy), links);
            }

            //Down
            start = chunk.cells[x][chunk.numCellsY - 1];
            dx = 0;
            dy = 1;
            if (start.region.regionId == parent.current.regionId && start.canMove(dx, dy)) {
                addRegionLink(start, start.getLink(dx, dy), links);
            }
        }

        for (int y = 0; y < chunk.numCellsY; y++) {
            //Left
            start = chunk.cells[0][y];
            dx = -1;
            dy = 0;
            if (start.region.regionId == parent.current.regionId && start.canMove(dx, dy)) {
                addRegionLink(start, start.getLink(dx, dy), links);
            }

            //Right
            start = chunk.cells[chunk.numCellsX - 1][y];
            dx = 1;
            dy = 0;
            if (start.region.regionId == parent.current.regionId && start.canMove(dx, dy)) {
                addRegionLink(start, start.getLink(dx, dy), links);
            }
        }

        if (diagonal) {
            //Down Left
            start = chunk.cells[0][0];
            dx = -1;
            dy = -1;
            if (start.region.regionId == parent.current.regionId && start.canMove(dx, dy)) {
                addRegionLink(start, start.getLink(dx, dy), links);
            }

            //Up Right
            start = chunk.cells[chunk.numCellsX - 1][chunk.numCellsY - 1];
            dx = 1;
            dy = 1;
            if (start.region.regionId == parent.current.regionId && start.canMove(dx, dy)) {
                addRegionLink(start, start.getLink(dx, dy), links);
            }

            //Up Left
            start = chunk.cells[0][chunk.numCellsY - 1];
            dx = -1;
            dy = 1;
            if (start.region.regionId == parent.current.regionId && start.canMove(dx, dy)) {
                addRegionLink(start, start.getLink(dx, dy), links);
            }

            //Down Right
            start = chunk.cells[chunk.numCellsX - 1][0];
            dx = 1;
            dy = -1;
            if (start.region.regionId == parent.current.regionId && start.canMove(dx, dy)) {
                addRegionLink(start, start.getLink(dx, dy), links);
            }
        }

    }

    public void getLinksLegacy(WorldChunk chunk, RegionLinks parent, Array<RegionLinks> links) {
        Cell start;
        Cell end;

        for (int x = 0; x < chunk.numCellsX; x++) {
            //TOP
            start = chunk.cells[x][0];
            if (start.region.regionId == parent.current.regionId) {
                end = chunk.world.getCellByCellId(start.cellId.x, start.cellId.y - 1);
                addRegionLink(start, end, links);
            }

            //Bottom
            start = chunk.cells[x][chunk.numCellsY - 1];
            if (start.region.regionId == parent.current.regionId) {
                end = chunk.world.getCellByCellId(start.cellId.x, start.cellId.y + 1);
                addRegionLink(start, end, links);
            }
        }

        for (int y = 0; y < chunk.numCellsY; y++) {
            //Left
            start = chunk.cells[0][y];
            if (start.region.regionId == parent.current.regionId) {
                end = chunk.world.getCellByCellId(start.cellId.x - 1, start.cellId.y);
                addRegionLink(start, end, links);
            }

            //Right
            start = chunk.cells[chunk.numCellsX - 1][y];
            if (start.region.regionId == parent.current.regionId) {
                end = chunk.world.getCellByCellId(start.cellId.x + 1, start.cellId.y);
                addRegionLink(start, end, links);
            }
        }
    }

    public void link(int dx, int dy) {

    }

    protected RegionLinks createRegion() {
//        RegionLinks region = Pools.obtain(RegionLinks.class);
//        region.source = Pools.obtain(RegionNode.class);
//        region.current = Pools.obtain(RegionNode.class);
        RegionLinks region = new RegionLinks();
        region.source = new RegionNode();
        region.current = new RegionNode();
//        createdRegionLinks.add(region);
        return region;
    }

    protected void addRegionLink(Cell a, Cell b, Array<RegionLinks> links) {
        if (a != null
                && b != null
                && a.region.regionId != WorldChunkRegionProcessor.RESET_LAYER_ID
                && b.region.regionId != WorldChunkRegionProcessor.RESET_LAYER_ID
        ) {
            RegionLinks link = createRegion();
            link.source.set(a);
            link.current.set(b);
            link.parent = null;
            if (!links.contains(link, false)) {
                links.add(link);
            }
        }
    }
}
