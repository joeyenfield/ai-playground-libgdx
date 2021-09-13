package com.emptypocketstudios.boardgame.engine.pathfinding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.PathFollowComponent;
import com.emptypocketstudios.boardgame.engine.messages.Message;
import com.emptypocketstudios.boardgame.engine.messages.MessageProcessor;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingRequest;
import com.emptypocketstudios.boardgame.engine.pathfinding.message.PathFindingResponse;

import java.util.PriorityQueue;

public class PathFinderManager implements MessageProcessor<Message> {
    public static final String MESSAGE_TARGET_NAME = "PATHFINDER";

    public Engine engine;
    PathFinder pathFinder;
    PriorityQueue<PathFindingRequest> requests = new PriorityQueue<>();
    long start = 0;

    public PathFinderManager(Engine engine) {
        this.engine = engine;
        this.pathFinder = new PathFinder(engine);
        engine.postOffice.register(MESSAGE_TARGET_NAME, this);
    }

    @Override
    public boolean handleMessage(Message message) {
        Gdx.app.debug("PATH-FINDER", "Got Message : " + message);
        if (message instanceof PathFindingRequest) {
            requests.add((PathFindingRequest) message);
            return true;
        }
        return false;
    }


    /**
     * @param maxTime - maximum time a query can be
     */
    public void process(final long maxTime) {
        start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < maxTime && requests.size() > 0) {
            final PathFindingRequest request = requests.poll();
            request.attempts--;
            //Identify if should use global finder or a new one ( for debugging )
            PathFinder finder = null;
            if (request.debug) {
                Entity entity = engine.getEntityByName(request.source);
                PathFollowComponent pathFollow = entity.getEntityComponent(PathFollowComponent.class);
                if (pathFollow.debugPathFinder == null) {
                    pathFollow.debugPathFinder = new PathFinder(engine);
                }
                finder = pathFollow.debugPathFinder;
            } else {
                Entity entity = engine.getEntityByName(request.source);
                PathFollowComponent pathFollow = entity.getEntityComponent(PathFollowComponent.class);
                if (pathFollow.debugPathFinder != null) {
                    pathFollow.debugPathFinder = null;
                }
            }
            if (finder == null) {
                finder = pathFinder;
            }

            //Perform Search
            PathFindingResponse response = finder.performSearch(request, maxTime - (System.currentTimeMillis() - start));
            //Process Result
            if (response.cellSearchResult == PathFindingResultEnum.TIMEOUT && request.attempts > 0) {
                Gdx.app.log("PATH-FINDER", "Timeout - Will Retry");
                //Add a retry to put the request up again
                requests.add(request);
                Pools.free(response);
            } else {
                // Send response
                response.setSource(request);
                engine.postOffice.send(response);
            }
        }
    }

    public int getMessageCount() {
        return requests.size();
    }
}
