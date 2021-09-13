package com.emptypocketstudios.boardgame.engine.pathfinding.layers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.PriorityQueue;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.ReflectionPool;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFindingResultEnum;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;
import com.emptypocketstudios.boardgame.engine.world.WorldChunkRegionNodeLink;
import com.emptypocketstudios.boardgame.library.RectangeUtils;

import java.util.HashMap;

public class RegionNodePathFinderNG {
    long start = 0;
    long lastTime = 0;
    long limit = 30;

    Pool<RegionLinksNG> linksPool = new ReflectionPool<>(RegionLinksNG.class);
    Array<RegionLinksNG> createdRegionLinks = new Array<RegionLinksNG>();


    PriorityQueue<RegionLinksNG> openRegions = new PriorityQueue<>();
    HashMap<RegionNode, RegionLinksNG> bestPath = new HashMap<>();

    boolean diagonal = false;

    public static void main(String[] args) {
        int PATHS = 10;
        int RUNS = 100;

        Engine e = new Engine();
        e.setup();
        e.update(1);
        RegionNodePathFinder old = new RegionNodePathFinder();
        RegionNodePathFinderNG newPath = new RegionNodePathFinderNG();

        Vector2 pos = new Vector2();
        Array<RegionNode> result = new Array<>(4096);
        for (int i = 0; i < PATHS; i++) {
            RectangeUtils.randomPoint(e.world.boundary, pos);
            Cell start = e.world.getCellAtWorldPosition(pos);
            RectangeUtils.randomPoint(e.world.boundary, pos);
            Cell end = e.world.getCellAtWorldPosition(pos);

            PathFindingResultEnum oldResult = PathFindingResultEnum.SUCCESS;
            PathFindingResultEnum newResult = PathFindingResultEnum.SUCCESS;
            long oldTime = 0;
            for (int j = 0; j < RUNS; j++) {
                result.clear();
                long startTime = System.nanoTime();
                oldResult = old.search(e.world, start, end, result, 10000, false);
                oldTime += System.nanoTime() - startTime;
            }

            long newTime = 0;
            for (int j = 0; j < RUNS; j++) {
                result.clear();
                long startTime = System.nanoTime();
                newResult = newPath.search(e.world, start, end, result, 10000, false);
                newTime += System.nanoTime() - startTime;
            }
            float meanOld = (oldTime / (float) RUNS) / 1e9f * 1000000;
            float meanNew = (newTime / (float) RUNS) / 1e9f * 1000000;
            System.out.printf("OLD [%3.1f ms] - NEW [%3.1f]", meanOld, meanNew);
            if (meanOld < meanNew) {
                System.out.println(" - FAIL ");
            } else {
                System.out.println("");
            }
            if (newResult != oldResult) {
                System.out.println("Fuck!!! WAS : " + oldResult + " NOW: " + newResult);
            }

        }

    }

    public PathFindingResultEnum search(World world, Cell startCell, Cell targetCell, Array<RegionNode> path,
                                        long timeLimit, boolean diagonal) {
        PathFindingResultEnum result;
        System.out.println("\n\n\n\n###############################################\nsearch");
        this.limit = timeLimit;
        this.diagonal = diagonal;

        start = System.currentTimeMillis();
        RegionNode startRegionNode = startCell.region;
        RegionNode targetRegionNode = targetCell.region;

        if (startRegionNode.equals(targetRegionNode)) {
            path.add(startRegionNode);
            result = PathFindingResultEnum.SUCCESS;
        } else {
            result = findPath(world, startRegionNode, targetRegionNode, path);
        }

        linksPool.freeAll(createdRegionLinks);
        bestPath.clear();
        openRegions.clear();
        lastTime = System.currentTimeMillis() - start;
        return result;
    }

    public boolean reachedLimit() {
        return System.currentTimeMillis() - start > limit;
    }

    protected PathFindingResultEnum findPath(World world, RegionNode startRegionNode, RegionNode targetRegionNode, Array<RegionNode> path) {
        //Get all links for a node
        System.out.println("findPath");
        WorldChunk chunk = world.getChunkByChunkId(startRegionNode.chunkId);
        Array<RegionLinksNG> chunkLinks = new Array<>();
        getLinks(chunk, startRegionNode, chunkLinks);
        for (int i = 0; i < chunkLinks.size; i++) {
            RegionLinksNG nextLink = chunkLinks.get(i);
            workOutWeight(1, nextLink, targetRegionNode);
            openRegions.add(nextLink);
        }


        while (openRegions.size() > 0) {
            if (reachedLimit()) {
                return PathFindingResultEnum.TIMEOUT;
            }
            RegionLinksNG currentLink = openRegions.poll();
            //Reached Target
            if (currentLink.chunkLink.current.equals(targetRegionNode)) {
                return workOutPath(currentLink, path);
            }

            chunk = world.getChunkByChunkId(currentLink.chunkLink.current.chunkId);
            getLinks(chunk, currentLink.chunkLink.current, chunkLinks);
            for (int i = 0; i < chunkLinks.size; i++) {
                RegionLinksNG nextLink = chunkLinks.get(i);
                nextLink.parent = currentLink;
                workOutWeight(currentLink.travelWeight, nextLink, targetRegionNode);
                if (bestPath.containsKey(nextLink.chunkLink.current)) {
                    //Get previous
                    RegionLinksNG bestLink = bestPath.get(nextLink.chunkLink.current);
                    if (bestLink.weight > nextLink.weight) {
                        bestPath.put(nextLink.chunkLink.current, nextLink);
                        openRegions.add(nextLink);
                    }
                } else {
                    bestPath.put(nextLink.chunkLink.current, nextLink);
                    openRegions.add(nextLink);
                }
            }
            chunkLinks.clear();
        }
        return PathFindingResultEnum.NOT_FOUND;
    }

    protected PathFindingResultEnum workOutPath(RegionLinksNG regionLink, Array<RegionNode> path) {
        while (regionLink != null) {
            path.add(regionLink.chunkLink.current);
            if(regionLink.parent == null || regionLink.parent.equals(regionLink)){
                path.add(regionLink.chunkLink.source);
                regionLink = null;
            }else {
                regionLink = regionLink.parent;
            }
            if (reachedLimit()) {
                path.clear();
                return PathFindingResultEnum.TIMEOUT;
            }
        }
        return PathFindingResultEnum.SUCCESS;
    }

    protected void workOutWeight(float existingWeight, RegionLinksNG current, RegionNode target) {
        current.travelWeight = existingWeight+1;
        current.destWeight = Math.min(
                Math.abs(target.chunkId.x - current.chunkLink.current.chunkId.x),
                Math.abs(target.chunkId.y - current.chunkLink.current.chunkId.y)
        );
//        float dx = Math.abs(current.chunkLink.current.chunkId.x - target.chunkId.x);
//        float dy = Math.abs(current.chunkLink.current.chunkId.y - target.chunkId.y);
//        current.destWeight= (dx + dy) + (1 - 2 ) * Math.min(dx, dy);
        current.weight = current.travelWeight + current.destWeight;
    }

    protected void getLinks(WorldChunk chunk, RegionNode parent, Array<RegionLinksNG> links) {
        for (int i = 0; i < chunk.regionNodeLinks.size; i++) {
            WorldChunkRegionNodeLink link = chunk.regionNodeLinks.get(i);
            if (link.source.equals(parent)) {
                RegionLinksNG regionLink = createRegion();
                regionLink.chunkLink = link;
                if (!links.contains(regionLink, false)) {
                    links.add(regionLink);
                }
            }
        }

        if (diagonal) {
            for (int i = 0; i < chunk.regionNodeDiagonalLinks.size; i++) {
                WorldChunkRegionNodeLink link = chunk.regionNodeDiagonalLinks.get(i);
                if (link.source.equals(parent)) {
                    RegionLinksNG regionLink = createRegion();
                    regionLink.chunkLink = link;
                    if (links.contains(regionLink, false)) {
                        links.add(regionLink);
                    }
                }
            }
        }
    }

    protected RegionLinksNG createRegion() {
        RegionLinksNG region = linksPool.obtain();
        createdRegionLinks.add(region);
        return region;
    }
}
