package com.emptypocketstudios.boardgame.engine.pathfinding.layers;

import com.badlogic.gdx.utils.Array;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFindingResultEnum;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;
import com.emptypocketstudios.boardgame.engine.world.World;

public abstract class RegionNodePathFinder {

    public abstract PathFindingResultEnum search(World world, Cell startCell, Cell targetCell, Array<RegionNode> path,
                                                 long timeLimit, boolean diagonal);
}
