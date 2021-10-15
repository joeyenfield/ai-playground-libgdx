package com.emptypocketstudios.boardgame.engine.entity.components.movement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.EntityComponent;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFinder;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFindingResultEnum;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingRequest;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingResponse;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingResponseHandler;
import com.emptypocketstudios.boardgame.engine.world.Cell;

public class PathFollowComponent extends EntityComponent implements Pool.Poolable, PathFindingResponseHandler {
    public Array<Cell> path = new Array<>(1024);
    public int currentIndex = 0;
    private PathFinder debugPathFinder = null;
    float moveThreshold = 0.05f; //What % of a call before close enough
    float endThreshold = 0.005f; // What % of cell for final

    long pathCreationTime = 0;
    boolean pathInProgress = false;
    boolean pathFollowFailed = false;
    boolean reachedTarget = false;

    Vector2 vel = new Vector2();

    public PathFollowComponent(Entity entity) {
        super(entity);
    }

    public void followPath(Array<Cell> path) {
        this.currentIndex = 0;
        this.path.clear();
        this.path.addAll(path);
        this.pathCreationTime = System.currentTimeMillis();
        this.pathFollowFailed = false;
        this.reachedTarget = false;
        this.pathInProgress = true;
    }

    private Cell getPathWithOffset(int offset) {
        Cell cell;
        if (currentIndex + offset < path.size) {
            cell = path.get(currentIndex + offset);
        } else {
            cell = null;
        }
        return cell;
    }

    private Cell getNextCell() {
        return getPathWithOffset(1);
    }

    @Override
    public void reset() {
        this.currentIndex = 0;
        this.path.clear();
        debugPathFinder = null;
        pathFollowFailed = false;
        reachedTarget = false;
        pathInProgress = false;
    }

    public Cell getLastCell() {
        return this.path.get(this.path.size - 1);
    }

    public void travelTo(Cell c, boolean diagonals, Boolean distCheckFast, boolean debug) {
        if (c == null) {
            return;
        }
        PathFindingRequest request = Pools.obtain(PathFindingRequest.class);

        request.diagonal = diagonals;
        request.distCheckFast = distCheckFast;
        request.source = entity.name;
        request.attempts = 2;
        request.pathFindingGoal.set(c.pos);
        request.setDebug(debug);
        request.directResponse = this;
        entity.world.engine.postOffice.send(request);
    }

    @Override
    public void processResponse(PathFindingResponse response) {
        if (response.cellSearchResult == PathFindingResultEnum.SUCCESS) {
            followPath(response.path);
        }
        Pools.free(response);
    }

    @Override
    public void update(float timeDiff) {
        if (!pathInProgress) {
            return;
        }
        //Peak next node
        Cell currentCell = getPathWithOffset(0);
        Cell nextCell = getPathWithOffset(1);

        Cell entityCell = entity.getCurrentCell();

        if (entityCell == null || entityCell.type == null) {
            failedPath();
        }
        MovementComponent move = entity.getEntityComponent(MovementComponent.class);
        if (move != null && nextCell != null) {
            if (nextCell.pos.dst2(entity.pos) <
                    (entity.world.engine.config.cellSize * moveThreshold) * (entity.world.engine.config.cellSize * moveThreshold)
            ) {
                currentIndex++;
                //Check if next cell has been updated and blocked -> if so stop and get new path to end
                Cell newCell = getNextCell();
                if (newCell != null && newCell.lastChangeTime > pathCreationTime) {
                    if (newCell.type.blocked) {
                        //Try find target
                        failedPath();
                    }
                }
            }
            float dx = nextCell.pos.x - entity.pos.x;
            float dy = nextCell.pos.y - entity.pos.y;
            float speed = move.maxSpeed / entityCell.type.getTravelEffort(entityCell.typeVariant, entityCell.isRoad);
            vel.set(dx, dy).setLength(speed);
        } else {
            float distance2 = currentCell.pos.dst2(entity.pos);
            float moveVel = move.maxSpeed / entityCell.type.getTravelEffort(entityCell.typeVariant, entityCell.isRoad);
            float moveStep = moveVel * timeDiff;
            if (moveStep * moveStep < distance2) {
                float dx = currentCell.pos.x - entity.pos.x;
                float dy = currentCell.pos.y - entity.pos.y;
                vel.set(dx, dy).setLength(moveVel);
            } else {
                finishPath();
            }
        }
    }

    public boolean isFollowingPath() {
        return pathInProgress;
    }

    public boolean isPathFollowFailed() {
        return pathFollowFailed;
    }

    private void failedPath() {
        vel.set(0, 0);
        this.pathFollowFailed = true;
        this.reachedTarget = false;
        this.pathInProgress = false;
    }

    private void finishPath() {
        vel.set(0, 0);
        this.pathFollowFailed = false;
        this.reachedTarget = true;
        this.pathInProgress = false;
    }

    public PathFinder getDebugPathFinder() {
        return debugPathFinder;
    }

    public void setDebugPathFinder(PathFinder debugPathFinder) {
        this.debugPathFinder = debugPathFinder;
    }


}
