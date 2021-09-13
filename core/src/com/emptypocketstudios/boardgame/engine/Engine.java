package com.emptypocketstudios.boardgame.engine;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.EntityType;
import com.emptypocketstudios.boardgame.engine.messages.Message;
import com.emptypocketstudios.boardgame.engine.messages.MessageProcessor;
import com.emptypocketstudios.boardgame.engine.messages.PostOffice;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFinderManager;
import com.emptypocketstudios.boardgame.engine.world.MapGenerator;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.library.RectangeUtils;
import com.emptypocketstudios.boardgame.ui.EngineControllerManager;

public class Engine implements MessageProcessor {
    public static final String ENGINE_NAME = "Engine";
    public static final int cellsPerChunkX = 15;
    public static final int cellsPerChunkY = 15;
    public static final int chunksX = 15;
    public static final int chunksY = 30;
    public static final int cellSize = 32; //pixels per cell
    public static final int entities = 500;

    public World world;
    public PostOffice<Message> postOffice = new PostOffice<>();
    public EngineControllerManager engineControllerManager;
    public PathFinderManager pathFinderManager;

    public Engine() {
        postOffice.register(ENGINE_NAME, this);
        pathFinderManager = new PathFinderManager(this);
    }

    public void setup() {
        Rectangle region = new Rectangle(-cellSize * chunksX * cellsPerChunkX / 2, -cellSize * chunksY * cellsPerChunkY / 2, cellSize * chunksX * cellsPerChunkX, cellSize * chunksY * cellsPerChunkY);
        world = new World(region, chunksX, chunksY, cellsPerChunkX, cellsPerChunkY);
        world.engine = this;
        world.loadAllChunks();
        MapGenerator.defaultMap(this);

        for(int i = 0 ;i < entities; i++) {
            Entity entity = new Entity();
            entity.init(world, EntityType.MAN);
            RectangeUtils.randomPoint(region, entity.pos);
            world.addEntity(entity);
        }
    }

    public void update(float delta) {
        world.update(delta);
        if(engineControllerManager != null) {
            engineControllerManager.updateLogic(delta);
        }
        postOffice.process((1000/60)/2);
        pathFinderManager.process(1000/60);
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    public Entity getEntityNearPos(Vector2 pos) {
        return world.getEntityByPos(pos);
    }

    public Entity getEntityByName(String entityName) {
        return world.getEntityByName(entityName);
    }
}
