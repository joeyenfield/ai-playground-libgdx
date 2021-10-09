package com.emptypocketstudios.boardgame.engine.pathfinding.layers;

import com.emptypocketstudios.boardgame.engine.pathfinding.PathFindingResultEnum;

public interface PathSearch {
    PathFindingResultEnum processAll();
    boolean processNext();
    boolean hasNext();
    PathFindingResultEnum getResult();
}
