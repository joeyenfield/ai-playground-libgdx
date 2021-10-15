package com.emptypocketstudios.boardgame.engine.pathfinding.layers;

import com.badlogic.gdx.ai.msg.PriorityQueue;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFindingResultEnum;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;
import com.emptypocketstudios.boardgame.engine.world.World;

import java.util.HashMap;

public class RegionNodePathFinderNG implements PathSearch {

    Array<RegionLinksNG> createdPaths = new Array<>();

    PriorityQueue<RegionLinksNG> openPaths = new PriorityQueue<>();
    HashMap<RegionNode, RegionLinksNG> bestIncomingLink = new HashMap<>();
    HashMap<RegionNode, Array<RegionNode>> worldRegionLinks;
    long startTime = 0;
    long timeLimit = 0;

    RegionNode regionBegin = null;
    RegionNode regionTarget = null;

    Array<RegionNode> resultPath;
    PathFindingResultEnum result = PathFindingResultEnum.NOT_FOUND;

    public void setup(World world, Cell startCell, Cell
            targetCell, Array<RegionNode> path, long timeLimit, boolean diagonal) {
        this.startTime = System.currentTimeMillis();
        this.timeLimit = timeLimit;
        this.regionBegin = startCell.region;
        this.regionTarget = targetCell.region;
        this.result = PathFindingResultEnum.IN_PROGRESS;
        this.resultPath = path;
        this.worldRegionLinks = diagonal ? world.diagonalRegionLinks : world.regionLinks;

        //Clear up previous runs

        Pools.freeAll(createdPaths);
        createdPaths.clear();
        bestIncomingLink.clear();
        openPaths.clear();

        //If same starting region -> return success
        if (regionBegin.equals(regionTarget)) {
            resultPath.add(regionBegin);
            result = PathFindingResultEnum.SUCCESS;
        } else {
            //Setup Initial seeds
            processNode(null, regionBegin, regionTarget);
        }
    }

    @Override
    public PathFindingResultEnum processAll() {
        while (processNext()) {
            if (outOfTime()) {
                result = PathFindingResultEnum.TIMEOUT;
            }
        }
        return getResult();
    }

    @Override
    public boolean processNext() {
        RegionLinksNG currentLink = openPaths.poll();
        if (currentLink != null) {
            if (currentLink.regionEnd.equals(regionTarget)) {
                backtrack(currentLink, resultPath);
            } else {
                processNode(currentLink, currentLink.regionEnd, regionTarget);
            }
        } else {
            if (result == PathFindingResultEnum.IN_PROGRESS) {
                result = PathFindingResultEnum.NOT_FOUND;
            }
        }
        return hasNext();
    }

    @Override
    public boolean hasNext() {
        return openPaths.size() > 0 && result == PathFindingResultEnum.IN_PROGRESS;
    }

    @Override
    public PathFindingResultEnum getResult() {
        return result;
    }

    private void backtrack(RegionLinksNG currentLink, Array<RegionNode> path) {
        do {
            if (outOfTime()) {
                result = PathFindingResultEnum.TIMEOUT;
                return;
            }
            path.add(currentLink.regionEnd);
            if (currentLink.parent == null) {
                path.add(currentLink.regionStart);
            }
            currentLink = currentLink.parent;
        } while (currentLink != null);
        result = PathFindingResultEnum.SUCCESS;
    }

    public void processNode(RegionLinksNG parent, RegionNode regionStart, RegionNode
            regionTarget) {
        Array<RegionNode> linkedRegions = worldRegionLinks.get(regionStart);
        for (int i = 0; i < linkedRegions.size; i++) {
            RegionNode regionEnd = linkedRegions.get(i);
            //Dont add same region link (stops recursive loop)
            if (!regionEnd.equals(regionStart)) {
                RegionLinksNG link = createLink(parent, regionStart, regionEnd, regionTarget);
                RegionLinksNG oldLink = bestIncomingLink.get(regionEnd);
                //Never visited before
                if (oldLink == null || oldLink.weight > link.weight) {
                    openPaths.add(link);
                    bestIncomingLink.put(regionEnd, link);
                }
            }
        }
    }

    public RegionLinksNG createLink(RegionLinksNG parent, RegionNode regionStart, RegionNode
            regionEnd, RegionNode regionTarget) {
        RegionLinksNG link = Pools.obtain(RegionLinksNG.class);
        createdPaths.add(link);
        link.parent = parent;
        link.regionStart = regionStart;
        link.regionEnd = regionEnd;

        //Work out travel weight
        link.travelWeight = 0;
        if (parent != null) {
            link.travelWeight += parent.travelWeight;
        }
        link.travelWeight += regionEnd.regionWalkWeight + (regionStart.chunkId.dst2(regionEnd.chunkId) + 1);

        //Dest
        link.destWeight = regionEnd.chunkId.dst2(regionTarget.chunkId);

        //Total weight
        link.weight = link.travelWeight + link.destWeight;
        return link;
    }

    protected boolean outOfTime() {
        return System.currentTimeMillis() - startTime > timeLimit;
    }
}