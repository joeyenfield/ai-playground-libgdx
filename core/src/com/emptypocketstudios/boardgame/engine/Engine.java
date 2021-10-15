package com.emptypocketstudios.boardgame.engine;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.EntityFactory;
import com.emptypocketstudios.boardgame.engine.messages.Message;
import com.emptypocketstudios.boardgame.engine.messages.MessageProcessor;
import com.emptypocketstudios.boardgame.engine.messages.PostOffice;
import com.emptypocketstudios.boardgame.engine.pathfinding.PathFinderManager;
import com.emptypocketstudios.boardgame.engine.pathfinding.layers.RegionNodePathFinderNG;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.engine.world.map.MapFactory;
import com.emptypocketstudios.boardgame.engine.world.map.MapGenerator;
import com.emptypocketstudios.boardgame.ui.EngineControllerManager;
import com.emptypocketstudios.boardgame.ui.render.EngineRender;

public class Engine implements MessageProcessor {
    public static final String ENGINE_NAME = "Engine";
    public EngineSetupConfig config;

    public RandomUtil random;
    public World world;
    public PostOffice<Message> postOffice = new PostOffice<>();
    public EngineControllerManager engineControllerManager;
    public PathFinderManager pathFinderManager;
    public EntityFactory entityFactory;
    public RegionNodePathFinderNG region = new RegionNodePathFinderNG();
    public EngineRender render;

    public Engine() {
        postOffice.register(ENGINE_NAME, this);
        pathFinderManager = new PathFinderManager(this);
        entityFactory = new EntityFactory(this);
    }

    public void setup() {
        setup(new EngineSetupConfig());
    }

    public void setup(EngineSetupConfig config) {
        log("Engine.setup", "Setting up engine");
        this.config = config;
        this.random = new RandomUtil(config.randomSeed);
        Rectangle region = new Rectangle(
                -config.cellSize * config.chunksX * config.cellsPerChunkX / 2,
                -config.cellSize * config.chunksY * config.cellsPerChunkY / 2,
                config.cellSize * config.chunksX * config.cellsPerChunkX,
                config.cellSize * config.chunksY * config.cellsPerChunkY);
        log("Engine.setup", "Creating World");
        world = new World(region, config.chunksX, config.chunksY, config.cellsPerChunkX, config.cellsPerChunkY);
        world.setEngine(this);
        world.loadAllChunks();
        world.update(0.1f);

        log("Engine.setup", "Generate Map");
        MapGenerator generator = MapFactory.getGenerator(config.map);
        generator.createMap(this);

        log("Engine.setup", "Setup Complete");
    }

    public void update(float delta) {
        world.update(delta);
        if (engineControllerManager != null) {
            engineControllerManager.updateLogic(delta);
        }
        postOffice.process((1000 / 60) / 2);
        pathFinderManager.process(1000 / 60);
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

    public void log(String source, String message) {
        System.out.println(source + " : " + message);
    }

    public void log(String source, Entity entity, String message) {
        log(source, "entity:" + entity.name + " - " + message);
    }
}
