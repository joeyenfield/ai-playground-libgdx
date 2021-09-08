package com.emptypocketstudios.boardgame.ui.controllers;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.library.CameraHelper;
import com.emptypocketstudios.boardgame.ui.EngineControllerManager;

public abstract class EngineController implements GestureDetector.GestureListener, InputProcessor {
    boolean active = true;

    //Convert screen to engine
    Vector2 pos = new Vector2();
    CameraHelper helper = new CameraHelper();

    EngineControllerManager manager;
    Engine engine;
    Camera camera;

    public EngineController(EngineControllerManager manager, Engine engine, Camera camera, Skin skin) {
        this.manager = manager;
        this.engine = engine;
        this.camera = camera;
    }

    public void mapScreenToEngine(int screenX, int screenY, Vector2 out) {
        out.x = screenX;
        out.y = screenY;
        helper.screenToWorld(camera, out);
    }

    public void mapScreenToEngine(float screenX, float screenY, Vector2 out) {
        out.x = screenX;
        out.y = screenY;
        helper.screenToWorld(camera, out);
    }

    public abstract Button getControlButton();

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {
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
