package com.emptypocketstudios.boardgame.engine.pathfinding.message;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.emptypocketstudios.boardgame.engine.messages.Message;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFinder;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFinderManager;

public class PathFindingRequest extends Message implements Pool.Poolable, Comparable<PathFindingRequest> {
    public int attempts = 2;
    public Vector2 pathFindingGoal = new Vector2();
    public boolean debug;

    public boolean diagonal = false;
    public boolean distCheckFast = false;
    public int requestTime = 0;

    public int priority = 0;

    public PathFindingRequest() {
        target = PathFinderManager.MESSAGE_TARGET_NAME;
    }

    @Override
    public void reset() {
        attempts = 3;
        diagonal = false;
        distCheckFast = false;
        requestTime = 0;
        priority = 0;
        pathFindingGoal.set(0, 0);

    }

    @Override
    public int compareTo(PathFindingRequest o) {
        int order = Integer.compare(priority, o.priority);
        if (order == 0) {
            order = Integer.compare(requestTime, o.requestTime);
        }
        return order;
    }
}
