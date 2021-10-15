package com.emptypocketstudios.boardgame.engine.pathfinding.layers;

import com.badlogic.gdx.ai.msg.PriorityQueue;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFindingResultEnum;
import com.emptypocketstudios.boardgame.engine.pathfinding.cells.CellLink;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;
import com.emptypocketstudios.boardgame.library.StringUtils;

public class RegionLimitedCellPathFinder implements PathSearch {
    // This tracks all paths so we can free to the pool again
    public Pool<CellLink> cellLinkPool = new ReflectionPool<>(CellLink.class);
    public Array<CellLink> usedPath = new Array<>();

    //Paths that still need to be processed
    public PriorityQueue<CellLink> openPaths = new PriorityQueue<CellLink>();
    //Tracks the best path to a NODE to allow a node to be re-opened if a better path is found
    public ArrayMap<Cell, CellLink> bestPaths = new ArrayMap<>();

    long startCellSearchTime;
    long maxTime;

    public float searchTime = 0;
    public float cleanupTime = 0;

    boolean diagonal = false;
    public boolean distCheckFast = false;
    Array<RegionNode> allowedNodes;
    Array<Cell> outputPath;
    Cell sourceCell;
    Cell targetCell;

    PathFindingResultEnum result;

    public void setup(Cell origin, Cell target, Array<RegionNode> allowedNodes,
                      Array<Cell> path, long maxTime, boolean diagonal) {
        this.startCellSearchTime = System.currentTimeMillis();

        //CleanUP previous
        cleanup();

        this.result = PathFindingResultEnum.IN_PROGRESS;
        this.outputPath = path;
        this.sourceCell = origin;
        this.targetCell = target;
        this.allowedNodes = allowedNodes;
        this.diagonal = diagonal;
        this.maxTime = maxTime;


        if (origin.cellId.equals(target.cellId)) {
            path.add(origin);
            result = PathFindingResultEnum.SUCCESS;
        } else {
            //Setup root node
            CellLink root = getEmptyPath();
            root.weight = 0;
            root.travelWeight = 0;
            root.destinationWeight = 0;
            root.parent = null;
            root.originCell = null;
            root.currentCell = origin;
            openPaths.add(root);
        }
    }

    @Override
    public boolean hasNext() {
        return openPaths.size() > 0 && result == PathFindingResultEnum.IN_PROGRESS;
    }

    @Override
    public PathFindingResultEnum getResult() {
        return result;
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
        CellLink currentPath = openPaths.poll();
        if (currentPath != null) {
            Cell currentCell = currentPath.currentCell;
            if (currentCell == targetCell) {
                traceback(currentPath);
            } else {
                //Figure out linked Cells
                getLinkedCells(currentPath, currentCell);
            }
        } else {
            if (result == PathFindingResultEnum.IN_PROGRESS) {
                result = PathFindingResultEnum.NOT_FOUND;
            }
        }
        return hasNext();
    }

    private void getLinkedCells(CellLink currentPath, Cell currentCell) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (!(dx == 0 && dy == 0) && (diagonal || (dx * dy == 0))) {
                    if (currentCell.canMove(dx, dy)) {
                        Cell nextCell = currentCell.getLink(dx, dy);
                        if (allowedNodes == null || allowedNodes.contains(nextCell.region, false)) {
                            if (!nextCell.type.blocked) {
                                CellLink nextPath = getPath(currentPath, currentCell, nextCell);
                                //Check if node was already closed and if this is a better route
                                CellLink lastPath = bestPaths.get(nextCell);
                                if (lastPath == null || nextPath.weight < lastPath.weight) {
                                    openPaths.add(nextPath);
                                    bestPaths.put(nextCell, nextPath);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected float getDistance(Vector2 p1, Vector2 p2) {
        return p1.dst(p2);
    }


    protected float getDeltaDistance(Vector2 p1, Vector2 p2) {
        return Math.abs(p2.x - p1.x) + Math.abs(p2.y - p1.y);
    }

    protected float getCheboskyDistance(Vector2 p1, Vector2 p2) {
        return Math.min(Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));
    }

    protected CellLink getPath(CellLink parent, Cell parentCell, Cell childCell) {
        CellLink path = getEmptyPath();

        float travelWeight = parentCell.type.getTravelEffort(parentCell.typeVariant, parentCell.isRoad)
                + childCell.type.getTravelEffort(parentCell.typeVariant, childCell.isRoad);

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

    protected boolean outOfTime() {
        return System.currentTimeMillis() - startCellSearchTime > maxTime;
    }

    protected void traceback(CellLink p) {
        CellLink current = p;
        while (current != null) {
            if (outOfTime()) {
                result = PathFindingResultEnum.TIMEOUT;
                return;
            }
            outputPath.add(current.currentCell);
            current = current.parent;
        }
        result = PathFindingResultEnum.SUCCESS;
        outputPath.reverse();
    }

    protected void cleanup() {
        long cleanStart = System.currentTimeMillis();
        cellLinkPool.freeAll(usedPath);
        usedPath.clear();
        openPaths.clear();
        bestPaths.clear();

        cleanupTime = ((System.currentTimeMillis() - cleanStart));
    }

    protected CellLink getEmptyPath() {
        CellLink p = cellLinkPool.obtain();
        usedPath.add(p);
        return p;
    }

}
