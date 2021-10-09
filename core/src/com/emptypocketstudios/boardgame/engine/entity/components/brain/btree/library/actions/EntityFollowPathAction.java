package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.BrainComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;
import com.emptypocketstudios.boardgame.engine.entity.components.movement.PathFollowComponent;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingResponse;

public class EntityFollowPathAction extends BTLeafNode {
    public static final String COMMAND_NAME = "FOLLOW_PATH";
    String memoryPathName = "";
    boolean followingPath = false;

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void before() {
        followingPath = false;
    }

    @Override
    public void after() {
    }

    @Override
    public void parse(Command command) {
        memoryPathName = command.getArg("memoryPathName", "PATH");
    }

    @Override
    public BTResult process() {
        BrainComponent brainComponent = entity.getEntityComponent(BrainComponent.class);
        PathFollowComponent pathFollowComponent = entity.getEntityComponent(PathFollowComponent.class);

        if (!followingPath) {
            PathFindingResponse path = (PathFindingResponse) brainComponent.memory.getObject(memoryPathName);
            if (path == null) {
                throw new RuntimeException("No path found in memory - name[" + memoryPathName + "]");
            }
            followingPath = true;
            pathFollowComponent.processResponse(path);
            return BTResult.RUNNING;
        } else {
            if (pathFollowComponent.isFollowingPath()) {
                return BTResult.RUNNING;
            } else {
                followingPath = false;
                if (pathFollowComponent.isPathFollowFailed()) {
                    return BTResult.FAILURE;
                }
                return BTResult.SUCCESS;
            }
        }
    }
}
