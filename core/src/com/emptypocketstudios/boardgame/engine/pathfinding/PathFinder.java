package com.emptypocketstudios.boardgame.engine.pathfinding;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.pathfinding.layers.RegionLimitedCellPathFinder;
import com.emptypocketstudios.boardgame.engine.pathfinding.layers.RegionNodePathFinder;
import com.emptypocketstudios.boardgame.engine.pathfinding.layers.RegionNodePathFinderNG;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingRequest;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingResponse;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;

public class PathFinder {
    Engine engine;
    public RegionLimitedCellPathFinder graphFinder = new RegionLimitedCellPathFinder();
    public RegionNodePathFinder layerFinder = new RegionNodePathFinder();
    public Array<RegionNode> regions = new Array<>();

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

        if (start != null && end != null) {
            if (start != end) {
                //Perform Region Search
                regions.clear();
//                layerFinder = new RegionNodePathFinder();
                response.regionSearchResult = layerFinder.search(engine.world, start, end, regions, maxTime, request.diagonal);
                //Perform Cell Search
                if (response.regionSearchResult == PathFindingResultEnum.SUCCESS) {
                    graphFinder.distCheckFast = request.distCheckFast;
                    long timeLeft = maxTime - (System.currentTimeMillis() - startTime);
                    response.cellSearchResult = graphFinder.findLink(start, end, regions, response.path, timeLeft, request.diagonal);
                } else {
                    response.cellSearchResult = response.regionSearchResult;
                }
            }
        }
        response.pathCreationTime = System.currentTimeMillis();
        return response;
    }

}
