package com.emptypocketstudios.boardgame.engine.pathfinding.layers;

import com.badlogic.gdx.ai.msg.PriorityQueue;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFindingResultEnum;
import com.emptypocketstudios.boardgame.engine.pathfinding.cells.CellLink;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellTypes;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;

import java.util.HashMap;
import java.util.Map;

public class RegionLimitedCellPathFinder {
    // This tracks all paths so we can free to the pool again
    public Array<CellLink> usedPath = new Array<>();
    //Paths that still need to be processed
    public PriorityQueue<CellLink> openPaths = new PriorityQueue<CellLink>();
    //Tracks the best path to a NODE to allow a node to be re-opened if a better path is found
    public Map<Cell, CellLink> bestPaths = new HashMap<>();

    public float searchTime = 0;
    public float cleanupTime = 0;
    public boolean debug = true;
    boolean diagonal = false;
    public boolean distCheckFast = false;
    Array<RegionNode> allowedNodes;
    Array<Cell> outputPath;
    Cell sourceCell;
    Cell targetCell;

    Array<Cell> linkCells = new Array<>();

    public PathFindingResultEnum findLink(Cell origin, Cell target, Array<RegionNode> allowedNodes,
                                          Array<Cell> path, long leftTime, boolean diagonal) {
        long start = System.currentTimeMillis();
        // If debug cleanup at start
        if (debug) {
            start = System.currentTimeMillis();
            cleanup();
            cleanupTime = ((System.currentTimeMillis() - start));
        }

        this.outputPath = path;
        this.sourceCell = origin;
        this.targetCell = target;
        this.allowedNodes = allowedNodes;
        this.diagonal = diagonal;

        //Setup root node
        CellLink root = getEmptyPath();
        root.weight = 0;
        root.travelWeight = 0;
        root.destinationWeight = 0;
        root.parent = null;
        root.originCell = null;
        root.currentCell = origin;
        openPaths.add(root);

        boolean pathFound = false;

        while (openPaths.size() > 0) {
            if (processNextPath()) {
                pathFound = true;
                break;
            }
        }
        searchTime = ((System.currentTimeMillis() - start));

        // If debug cleanup at start
        if (!debug) {
            start = System.currentTimeMillis();
            cleanup();
            cleanupTime = ((System.currentTimeMillis() - start));
        }

        if(pathFound){
            return PathFindingResultEnum.SUCCESS;
        }else{
            return PathFindingResultEnum.NOT_FOUND;
        }
    }

    public boolean processNextPath() {
        CellLink currentPath = openPaths.poll();
        Cell currentCell = currentPath.currentCell;
        boolean pathFound = false;

        linkCells.clear();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (!(dx == 0 && dy == 0) && (diagonal || (dx * dy == 0))) {
                    if (currentCell.canMove(dx, dy)) {
                        linkCells.add(currentCell.getLink(dx, dy));
                    }
                }
            }
        }

        for (Cell nextCell : linkCells) {
            if (allowedNodes.contains(nextCell.region, false)) {
                //Make sure cell not blocked
                if (!CellTypes.isBlocked(nextCell.type)) {
                    CellLink nextPath = getPath(currentPath, currentCell, nextCell);
                    //If at target break out
                    if (nextCell == targetCell) {
                        traceback(nextPath);
                        pathFound = true;
                        break;
                    }

                    //Check if node was already closed and if this is a better route
                    CellLink lastPath = bestPaths.get(nextCell);
                    if (lastPath != null) {
                        if (nextPath.weight < lastPath.weight) {
                            openPaths.add(nextPath);
                            bestPaths.put(nextCell, nextPath);
                        }
                    } else {
                        openPaths.add(nextPath);
                        bestPaths.put(nextCell, nextPath);
                    }
                }
            }
        }
        return pathFound;
    }

    public float getDistance(Vector2 p1, Vector2 p2) {
        return p1.dst(p2);
    }


    public float getDeltaDistance(Vector2 p1, Vector2 p2) {
        return Math.abs(p2.x - p1.x) + Math.abs(p2.y - p1.y);
    }

    public float getCheboskyDistance(Vector2 p1, Vector2 p2) {
        return Math.min(Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));
    }

    protected CellLink getPath(CellLink parent, Cell parentCell, Cell childCell) {
        CellLink path = getEmptyPath();

        float travelWeight = CellTypes.getTravelEffort(parentCell.type)
                + CellTypes.getTravelEffort(childCell.type);

        travelWeight = getDeltaDistance(childCell.pos, parentCell.pos) * travelWeight;

        //Decreate travelWeight
        float destWeight = 0;
        if (distCheckFast) {
            destWeight = getCheboskyDistance(targetCell.pos, childCell.pos);
        } else {
            destWeight = getDeltaDistance(targetCell.pos, childCell.pos);
        }


        path.link(parent, parentCell, childCell,
                parent.travelWeight + travelWeight,
                destWeight);
        return path;
    }

    protected void traceback(CellLink p) {
        CellLink current = p;
        while (current != null) {
            outputPath.add(current.currentCell);
            current = current.parent;

        }
        outputPath.reverse();
    }

    protected void cleanup() {
        Pools.freeAll(usedPath, true);
        usedPath.clear();
        openPaths.clear();
        bestPaths.clear();
        linkCells.clear();
        sourceCell = null;
        targetCell = null;
        outputPath = null;
        allowedNodes = null;
    }

    protected CellLink getEmptyPath() {
        CellLink p = Pools.obtain(CellLink.class);
        usedPath.add(p);
        return p;
    }

}
