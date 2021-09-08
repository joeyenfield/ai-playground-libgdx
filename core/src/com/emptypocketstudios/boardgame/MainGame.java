package com.emptypocketstudios.boardgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.emptypocketstudios.boardgame.library.input.NamedInputMultiplexer;
import com.emptypocketstudios.boardgame.library.screens.LoadingScreen;
import com.emptypocketstudios.boardgame.library.screens.SplashScreen;
import com.emptypocketstudios.boardgame.library.ui.GameScreen;
import com.emptypocketstudios.boardgame.library.ui.Scene2DToolkit;
import com.emptypocketstudios.boardgame.ui.GameEngineScreen;

public class MainGame extends Game implements InputProcessor {
    protected AssetManager assetManager;
    protected LoadingScreen loadingScreen;
    protected SplashScreen splashScreen;
    GameScreen currentScreen;
    NamedInputMultiplexer input;

    @Override
    public void create() {
        Scene2DToolkit.getToolkit().reloadSkin();
        input = new NamedInputMultiplexer();
        Gdx.input.setInputProcessor(input);
        input.addProcessor(this, "this");
        assetManager = new AssetManager();
        loadingScreen = new LoadingScreen(this, input);
        splashScreen = new SplashScreen(this, input);
        loadScreen(new GameEngineScreen(this, input), false);
    }

    public void loadScreen(GameScreen gameScreen, boolean proceededOnLood) {
        if (currentScreen != null) {
            currentScreen.clearAssetManager(assetManager);
        }
        currentScreen = gameScreen;
        loadingScreen.setProceededOnLood(proceededOnLood);
        loadingScreen.setNextScreen(currentScreen);
        setScreen(gameScreen);
    }

    @Override
    public void dispose() {
        super.dispose();
        Gdx.input.setInputProcessor(null);
        setScreen(null);
        if (screen != null) {
            screen.dispose();
        }
        screen = null;
        Scene2DToolkit.getToolkit().disposeSkin();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void pause() {
        super.pause();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}

