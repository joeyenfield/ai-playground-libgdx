package com.emptypocketstudios.boardgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.emptypocketstudios.boardgame.MainGame;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.library.camera.PanAndZoomCamController;
import com.emptypocketstudios.boardgame.library.input.NamedInputMultiplexer;
import com.emptypocketstudios.boardgame.library.ui.Scene2DToolkit;
import com.emptypocketstudios.boardgame.library.ui.StageScreen;
import com.emptypocketstudios.boardgame.ui.render.EngineRender;

public class GameEngineScreen extends StageScreen {

    Engine engine;
    EngineRender render;

    Viewport viewport;
    OrthographicCamera camera;
    PanAndZoomCamController cameraController;


    public GameEngineScreen(MainGame game, NamedInputMultiplexer inputProcessor) {
        super(game, inputProcessor);

        //Setup Cameras
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        cameraController = new PanAndZoomCamController(camera);

        //Setup Engine
        engine = new Engine();
        engine.setup();
        engine.engineControllerManager = new EngineControllerManager(engine, camera);
        ;
        //Setup Renders
        render = new EngineRender(new TextureAtlas(Gdx.files.internal("art/game.atlas")));

    }

    @Override
    public void addInputMultiplexer(NamedInputMultiplexer input) {
        super.addInputMultiplexer(input);
        input.addProcessor(engine.engineControllerManager.inputMultiplexer, "EngineController");
        input.addProcessor(cameraController, "Camera");
    }

    @Override
    public void createStage(Stage stage) {
        stage.addActor(engine.engineControllerManager);
    }

    @Override
    public void updateLogic(float delta) {
        super.updateLogic(delta);
        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
        engine.engineControllerManager.resize(width, height);
    }

    @Override
    public void drawScreen(float delta) {
        camera.update();
        ScreenUtils.clear(0, 1, 1, 1);
        render.render(viewport, engine);
    }

    @Override
    public void drawOverlay(float delta) {

    }

    @Override
    public void drawEventOverlay(float delta) {
        if (super.eventBatch == null) {
            eventBatch = new SpriteBatch();
        }
        eventBatch.setProjectionMatrix(overlayCamera.combined);
        BitmapFont font = getSkin().getFont(Scene2DToolkit.getDefaultSkinName());

        eventBatch.begin();

        int lineHeight = 25;
        font.draw(eventBatch, "FPS  : " + Gdx.graphics.getFramesPerSecond(), 0, -Gdx.graphics.getHeight() / 2 + 50);
        font.draw(eventBatch, "POST : " + engine.postOffice.getMessageCount(), 0, -Gdx.graphics.getHeight() / 2 + 50 + lineHeight);
        font.draw(eventBatch, "AI   :" + engine.pathFinderManager.getMessageCount(), 0, -Gdx.graphics.getHeight() / 2 + 50 + 2 * lineHeight);


        eventBatch.end();
    }

    @Override
    public void setupAssetManager(AssetManager assetManager) {
        engine.setup();
    }

    @Override
    public void clearAssetManager(AssetManager assetManager) {
//        TODO: Put this back in
//        this.render.atlas.dispose();
//        this.render.atlas = null;
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }
}
