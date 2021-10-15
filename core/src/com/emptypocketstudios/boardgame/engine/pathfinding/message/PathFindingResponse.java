package com.emptypocketstudios.boardgame.engine.pathfinding.message;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.emptypocketstudios.boardgame.engine.messages.Message;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFindingResultEnum;
import com.emptypocketstudios.boardgame.engine.world.Cell;

public class PathFindingResponse extends Message implements Pool.Poolable {
    public long pathCreationTime = 0;
    public Array<Cell> path = new Array<>(1024);

    public PathFindingResultEnum regionSearchResult = PathFindingResultEnum.NOT_FOUND;
    public PathFindingResultEnum cellSearchResult = PathFindingResultEnum.NOT_FOUND;

    public boolean diagonal = false;
    public boolean debug = false;
    public boolean distCheckFast = false;
    public int priority = 0;

    public void setSource(PathFindingRequest request) {
        this.diagonal = request.diagonal;
        this.distCheckFast = request.distCheckFast;
        this.debug = request.isDebug();
        this.priority = request.priority;
    }

    @Override
    public void reset() {
        path.clear();
        regionSearchResult = PathFindingResultEnum.NOT_FOUND;
        cellSearchResult = PathFindingResultEnum.NOT_FOUND;
    }

    @Override
    public String toString() {
        return "PathFindingResponse{" +
                "pathCreationTime=" + pathCreationTime +
                ", path=" + path.toString(",") +
                ", regionSearchResult=" + regionSearchResult +
                ", cellSearchResult=" + cellSearchResult +
                ", diagonal=" + diagonal +
                ", debug=" + debug +
                ", distCheckFast=" + distCheckFast +
                ", priority=" + priority +
                '}';
    }
}
