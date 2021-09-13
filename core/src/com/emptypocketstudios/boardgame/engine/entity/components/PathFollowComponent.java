package com.emptypocketstudios.boardgame.engine.entity.components;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.NotificationTypes;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFinder;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFindingResultEnum;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingRequest;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingResponse;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellTypes;

public class PathFollowComponent extends EntityComponent implements Pool.Poolable {
    public Array<Cell> path = new Array<>(1024);
    public PathFinder debugPathFinder = null;

    public int currentIndex = 0;
    public float moveThreshold = 0.05f; //What % of a call before close enough
    public float endThreshold = 0.005f; // What % of cell for final

    public boolean diagonal = false;
    public boolean debug = false;
    public boolean distCheckFast = false;
    public int priority = 0;
    public long pathCreationTime = 0;
    public boolean thinkingAboutPath = false;
    public PathFollowComponent(Entity entity) {
        super(entity);
    }

    public Cell getPathWithOffset(int offset) {
        Cell cell;
        if (currentIndex + offset < path.size) {
            cell = path.get(currentIndex + offset);
        } else {
            cell = null;
        }
        return cell;
    }

    public Cell getNextCell() {
        return getPathWithOffset(1);
    }

    public Cell getCurrentCell() {
        return entity.world.getCellAtWorldPosition(entity.pos);
    }

    public void handle(PathFindingResponse response) {
        thinkingAboutPath=false;
        reset();
        NotificationComponent notification = entity.getEntityComponent(NotificationComponent.class);
        if (response.cellSearchResult == PathFindingResultEnum.SUCCESS) {
            if (notification != null) {
                notification.notification = NotificationTypes.PATH_FOLLOW;
                notification.displayTime = 2;
            }
            path.addAll(response.path);
            pathCreationTime = response.pathCreationTime;
            diagonal = response.diagonal;
            debug = response.debug;
            distCheckFast = response.distCheckFast;
            priority = response.priority;
        } else {
            if (notification != null) {
                if (response.cellSearchResult == PathFindingResultEnum.NOT_FOUND) {
                    notification.notification = NotificationTypes.SAD;
                    notification.displayTime = -1;
                }
                if (response.cellSearchResult == PathFindingResultEnum.TIMEOUT) {
                    notification.notification = NotificationTypes.PATH_CONFUSED;
                    notification.displayTime = -1;
                }
            }
        }
        Pools.free(response);
    }

    @Override
    public void reset() {
        this.currentIndex = 0;
        this.path.clear();

        MovementComponent move = entity.getEntityComponent(MovementComponent.class);
        move.vel.set(0, 0);
    }

    public Cell getLastCell() {
        return this.path.get(this.path.size - 1);
    }

    public void travelTo(Cell c, boolean diagonals, Boolean distCheckFast, boolean debug) {
        if(c == null){
            return;
        }
        thinkingAboutPath=true;
        NotificationComponent notification = entity.getEntityComponent(NotificationComponent.class);
        if (notification != null) {
            notification.notification = NotificationTypes.THINKING;
            notification.displayTime = -1;
        }
        PathFindingRequest request = Pools.obtain(PathFindingRequest.class);
        request.diagonal = diagonals;
        request.distCheckFast = distCheckFast;
        request.source = entity.name;
        request.attempts = 2;
        request.pathFindingGoal.set(c.pos);
        request.debug = debug;
        entity.world.engine.postOffice.send(request);
    }

    @Override
    public void update(float timeDiff) {
        if (path == null || path.size == 0) {
            return;
        }
        //Peak next node
        Cell currentCell = getPathWithOffset(0);
        Cell nextCell = getPathWithOffset(1);

        MovementComponent move = entity.getEntityComponent(MovementComponent.class);
        if (nextCell != null) {
            if (nextCell.pos.dst2(entity.pos) <
                    (entity.world.engine.cellSize * moveThreshold) * (entity.world.engine.cellSize * moveThreshold)
            ) {
                currentIndex++;
                //Check if next cell has been updated and blocked -> if so stop and get new path to end
                Cell newCell = getNextCell();
                if (newCell != null && newCell.lastChangeTime > pathCreationTime) {
                    if (CellTypes.isBlocked(newCell.type)) {
                        //Try find target
                        travelTo(getLastCell(), diagonal, distCheckFast, debug);
                        reset();
                    }
                }

            }
            float dx = nextCell.pos.x - entity.pos.x;
            float dy = nextCell.pos.y - entity.pos.y;
            float speed = move.maxSpeed * (1f / CellTypes.getTravelEffort(getCurrentCell().type));
            move.vel.set(dx, dy).setLength(speed);
        } else {
            float dx = currentCell.pos.x - entity.pos.x;
            float dy = currentCell.pos.y - entity.pos.y;
            if (dx * dx + dy * dy > (entity.world.engine.cellSize * endThreshold) * (entity.world.engine.cellSize * endThreshold)) {
                float speed = move.maxSpeed * (1f / CellTypes.getTravelEffort(getCurrentCell().type));
                move.vel.set(dx, dy).setLength(speed);
            } else {
                finishPath();
            }
        }
    }

    public boolean isFollowingPath() {
        return currentIndex > 0 && path.size > 0;
    }

    private void finishPath() {
        NotificationComponent notification = entity.getEntityComponent(NotificationComponent.class);
        if (notification != null) {
            notification.notification = NotificationTypes.HAPPY;
            notification.displayTime = 5;
        }
        reset();
    }
}
