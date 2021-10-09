package com.emptypocketstudios.boardgame.engine.pathfinding;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.pathfinding.layers.RegionLimitedCellPathFinder;
import com.emptypocketstudios.boardgame.engine.pathfinding.layers.RegionNodePathFinderNG;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingRequest;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingResponse;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;

public class PathFinder {
    public boolean debug=false;
    Engine engine;
    public RegionLimitedCellPathFinder graphFinder = new RegionLimitedCellPathFinder();
    public RegionNodePathFinderNG layerFinder = new RegionNodePathFinderNG();

    public Array<RegionNode> regions = new Array<>();
    public Array<Cell> cells = new Array<>();

    public PathFinder(Engine engine) {
        this.engine = engine;
    }


    public PathFindingResponse performSearch(PathFindingRequest request, long maxTime) {
        long startTime = System.currentTimeMillis();

        request.attempts--;
        //Find Entity by Name
        Entity entity = engine.world.getEntityByName(request.source);

        Cell start = engine.world.getCellAtWorldPosition(entity.pos);
        Cell end = engine.world.getCellAtWorldPosition(request.pathFindingGoal);

        PathFindingResponse response = Pools.obtain(PathFindingResponse.class);
        response.source = PathFinderManager.MESSAGE_TARGET_NAME;
        response.target = entity.name;

        getPathFindingResponse(maxTime, startTime, response, start, end, request.diagonal, request.distCheckFast);
        return response;
    }


    public void getPathFindingResponse(Cell start, Cell end, PathFindingResponse response) {
        getPathFindingResponse(999999, System.currentTimeMillis(), response, start, end, false, false);
    }

    public void getPathFindingResponse(Cell start, Cell end, PathFindingResponse response, boolean diagonals) {
        getPathFindingResponse(999999, System.currentTimeMillis(), response, start, end, diagonals, false);
    }

    public void getPathFindingResponse(long maxTime, long startTime, PathFindingResponse response, Cell start, Cell end, boolean diagonal, boolean distCheckFast) {
        if (start != null && end != null) {
            //Perform Region Search
            regions.clear();
            cells.clear();

            layerFinder.setup(engine.world, start, end, regions, maxTime, diagonal);
            response.regionSearchResult = layerFinder.processAll();
            //Perform Cell Search
            if (response.regionSearchResult == PathFindingResultEnum.SUCCESS) {
                graphFinder.distCheckFast = distCheckFast;
                long timeLeft = maxTime - (System.currentTimeMillis() - startTime);
                graphFinder.setup(start, end, regions, response.path, timeLeft, diagonal);
                response.cellSearchResult = graphFinder.processAll();
            } else {
                response.cellSearchResult = response.regionSearchResult;
            }
        }
        response.pathCreationTime = System.currentTimeMillis();
    }
}
