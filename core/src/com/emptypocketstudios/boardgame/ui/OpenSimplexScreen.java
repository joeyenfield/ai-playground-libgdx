package com.emptypocketstudios.boardgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.emptypocketstudios.boardgame.MainGame;
import com.emptypocketstudios.boardgame.library.CameraHelper;
import com.emptypocketstudios.boardgame.library.RectangleUtils;
import com.emptypocketstudios.boardgame.library.camera.PanAndZoomCamController;
import com.emptypocketstudios.boardgame.library.input.NamedInputMultiplexer;
import com.emptypocketstudios.boardgame.library.noise.NoiseGenerator;
import com.emptypocketstudios.boardgame.library.ui.Scene2DToolkit;
import com.emptypocketstudios.boardgame.library.ui.StageScreen;
import com.emptypocketstudios.boardgame.ui.controllers.noise.NoiseDialog;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class OpenSimplexScreen extends StageScreen {
    NoiseGenerator noise = new NoiseGenerator();
    Viewport viewport;
    OrthographicCamera camera;
    PanAndZoomCamController cameraController;

    PolygonSpriteBatch batch;
    ShapeDrawer drawer;
    TextureRegion region;
    Texture texture;
    CameraHelper helper;
    Pixmap texturePixmap;
    NoiseDialog layerDialog;

    public OpenSimplexScreen(MainGame game, NamedInputMultiplexer inputProcessor) {
        super(game, inputProcessor);

        layerDialog = new NoiseDialog(noise, "Layers");
        //Setup Cameras
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        cameraController = new PanAndZoomCamController(camera);
        helper = new CameraHelper();

        batch = new PolygonSpriteBatch();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        texture = new Texture(pixmap); //remember to dispose of later
        pixmap.dispose();
        region = new TextureRegion(texture, 0, 0, 1, 1);
        drawer = new ShapeDrawer(batch, region);

        cameraController.setMaxZoom(10000);
        cameraController.setMinZoom(1 / 10000);
        texturePixmap = new Pixmap(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Pixmap.Format.RGB565);
    }

    @Override
    public void addInputMultiplexer(NamedInputMultiplexer input) {
        super.addInputMultiplexer(input);
        input.addProcessor(new InputAdapter(){
            Vector2 p = new Vector2();
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                p.x = screenX;
                p.y = screenY;
                helper.screenToWorld(camera, p);
                noise.getValue(p.x, p.y, true);
                return super.touchDown(screenX, screenY, pointer, button);
            }
        },"value");
        input.addProcessor(cameraController, "Camera");
    }

    @Override
    public void createStage(Stage stage) {
        stage.addActor(layerDialog);
        layerDialog.setVisible(true);
    }

    @Override
    public void updateLogic(float delta) {
        super.updateLogic(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
        texturePixmap = new Pixmap(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Pixmap.Format.RGB565);
    }

    public void getColor(float x, float y, Color c) {
        x = x / Gdx.graphics.getWidth();
        y = y / Gdx.graphics.getHeight();
        setColor(c, noise.getValue(x, y));
    }

    public void setColor(Color c, float value) {
        c.fromHsv((1 - value) * 250, 1, 1);
    }


    @Override
    public void drawScreen(float delta) {
        layerDialog.update();
        camera.update();
        ScreenUtils.clear(1, 1, 1, 1);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        Vector2 p1 = new Vector2();
        Vector2 p2 = new Vector2();
        Color topLeft = new Color(Color.GREEN);
        Color topRight = new Color(Color.RED);
        Color bottomRight = new Color(Color.WHITE);
        Color bottomLeft = new Color(Color.BLUE);
        Rectangle rect = new Rectangle();


        p1.x = 0;
        p1.y = 0;
        p2.x = 1;
        p2.y = 1;
        helper.screenToWorld(camera, p1);
        helper.screenToWorld(camera, p2);

        rect.x = p1.x;
        rect.y = p1.y;
        rect.width = p2.x - p1.x;
        rect.height = p2.y - p1.y;
        RectangleUtils.ensurePositive(rect);

        //PIXMAP
//        for (int x = 0; x < Gdx.graphics.getWidth(); x += stepSize) {
//            for (int y = 0; y < Gdx.graphics.getHeight(); y += stepSize) {
//                p1.x = x;
//                p1.y = y;
//                helper.screenToWorld(camera, p1);
//                getColor(p1.x,p1.y, bottomLeft);
//                texturePixmap.setColor(bottomLeft);
//                texturePixmap.drawPixel(x,y);
//            }
//        }
//        Texture t = new Texture(texturePixmap);
//
//        float insetX = rect.width*0.01f;
//        float insetY = rect.height*0.01f;
//        batch.draw(t,rect.x+insetX,rect.y+insetY,rect.width-2*insetX,rect.height-2*insetY);
////
        int stepSize = 5;
        for (int x = 0; x < Gdx.graphics.getWidth(); x += stepSize) {
            for (int y = 0; y < Gdx.graphics.getHeight(); y += stepSize) {
                p1.x = x;
                p1.y = y;
                p2.x = (x + stepSize);
                p2.y = (y + stepSize);
                helper.screenToWorld(camera, p1);
                helper.screenToWorld(camera, p2);
                rect.x = p1.x;
                rect.y = p1.y;
                rect.width = p2.x - p1.x;
                rect.height = p2.y - p1.y;
                RectangleUtils.ensurePositive(rect);
                getColor(rect.x, rect.y, bottomLeft);
                getColor(rect.x + rect.width, rect.y, bottomRight);
                getColor(rect.x, rect.y + rect.height, topLeft);
                getColor(rect.x + rect.width, rect.y + rect.height, topRight);

                if (y < 10) {
                    setColor(topRight, ((float) x / (float) Gdx.graphics.getWidth()));
                    setColor(topLeft, ((float) x / (float) Gdx.graphics.getWidth()));
                    setColor(bottomLeft, ((float) x / (float) Gdx.graphics.getWidth()));
                    setColor(bottomRight, ((float) x / (float) Gdx.graphics.getWidth()));
                }


                drawer.filledRectangle(rect, topRight, topLeft, bottomLeft, bottomRight);
            }
        }
        batch.end();
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
        eventBatch.end();
    }

    @Override
    public void setupAssetManager(AssetManager assetManager) {
    }

    @Override
    public void clearAssetManager(AssetManager assetManager) {
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
