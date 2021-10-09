package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.BrainComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFindingResultEnum;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingRequest;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingResponse;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingResponseHandler;

public class CellPlanPathAction
        extends BTLeafNode implements PathFindingResponseHandler {
    public static final String COMMAND_NAME = "PLAN_PATH";

    String memoryTargetPos = "";
    String memory = "";

    boolean diagonal = false;
    boolean debug = false;
    boolean distCheckFast = false;
    int priority = 0;

    boolean pathInProgress = false;
    boolean pathSearchInProgress = false;
    boolean pathSearchSuccess = false;

    Vector2 currentTarget = new Vector2();

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void before() {
        pathInProgress = false;
        pathSearchInProgress = false;
        pathSearchSuccess = false;
    }

    @Override
    public void after() {
    }

    @Override
    public void parse(Command command) {
        memory = command.getArg("memory", "PATH");
        memoryTargetPos = command.getArg("memoryTargetPos");
        debug = command.getArgBoolean("debug", false);
    }

    @Override
    public BTResult process() {
        if (!pathInProgress) {
            //Assume first entry - so perform search
            pathInProgress = true;
            pathSearchInProgress = true;
            pathSearchSuccess = true;

            BrainComponent brain = entity.getEntityComponent(BrainComponent.class);
            Vector2 pos = (Vector2) brain.memory.getObject(memoryTargetPos);
            currentTarget.set(pos);

            travelTo(currentTarget);
            return BTResult.RUNNING;
        }
        if (pathInProgress) {
            if (pathSearchInProgress) {
                return BTResult.RUNNING;
            } else {
                if (pathSearchSuccess) {
                    return BTResult.SUCCESS;
                }
            }
        }
        return BTResult.FAILURE;
    }

    public void travelTo(Vector2 pos) {
        PathFindingRequest request = Pools.obtain(PathFindingRequest.class);
        request.source = entity.name;
        request.diagonal = this.diagonal;
        request.distCheckFast = this.distCheckFast;
        request.debug = this.debug;
        request.attempts = 2;
        request.priority = this.priority;
        request.pathFindingGoal.set(pos);
        request.directResponse = this;
        entity.world.engine.postOffice.send(request);
    }

    @Override
    public void processResponse(PathFindingResponse response) {
        pathSearchInProgress = false;
        BrainComponent brain = entity.getEntityComponent(BrainComponent.class);
        if (response.cellSearchResult == PathFindingResultEnum.SUCCESS) {
            pathSearchSuccess = true;
            brain.memory.store(memory, response);
        } else {
            pathSearchSuccess = false;
            Pools.free(response);
        }
    }
}