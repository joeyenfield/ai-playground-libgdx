package com.emptypocketstudios.boardgame.engine.pathfinding.cells;

import com.badlogic.gdx.ai.msg.PriorityQueue;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CellPathFinder {
    // This tracks all paths so we can free to the pool again
    public Array<CellLink> usedPath = new Array<>();

    //Paths that still need to be processed
    public PriorityQueue<CellLink> openPaths = new PriorityQueue<CellLink>();

    //Tracks the best path to a NODE to allow a node to be re-opened if a better path is found
    public Map<Cell, CellLink> bestPaths = new HashMap<>();

    public float searchTime = 0;
    public float cleanupTime = 0;

    Array<Cell> outputPath;
    Cell sourceCell;
    Cell targetCell;


    public boolean findLink(Cell origin, Cell target, Array<Cell> path) {
        long start = System.currentTimeMillis();
        this.outputPath = path;
        this.sourceCell = origin;
        this.targetCell = target;

        //Setup root node
        CellLink root = getEmptyPath();
        root.weight = 0;
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

        start = System.currentTimeMillis();
        cleanup();
        cleanupTime = ((System.currentTimeMillis() - start));
        return pathFound;
    }

    public boolean processNextPath() {
        CellLink currentPath = openPaths.poll();
        Cell currentCell = currentPath.currentCell;
        boolean pathFound = false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 && dy != 0) {
                    Cell nextCell = currentCell.links[dx][dy];
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
        }
        return pathFound;
    }

    protected CellLink getPath(CellLink parent, Cell parentCell, Cell childCell) {
        CellLink path = getEmptyPath();

        float travelWeight = parentCell.pos.dst2(childCell.pos);

        //Chebosky
        float destWeight = Math.min(
                Math.abs(targetCell.pos.x - childCell.pos.x),
                Math.abs(targetCell.pos.y - childCell.pos.y)
        );
//        float destWeight = targetCell.pos.dst2(childCell.pos);

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
        sourceCell = null;
        targetCell = null;
        outputPath = null;
    }

    protected CellLink getEmptyPath() {
        CellLink p = Pools.obtain(CellLink.class);
        usedPath.add(p);
        return p;
    }

}
