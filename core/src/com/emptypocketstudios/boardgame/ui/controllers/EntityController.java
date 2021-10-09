package com.emptypocketstudios.boardgame.ui.controllers;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.ui.EngineControllerManager;

public class EntityController extends EngineController {

    public EntityController(EngineControllerManager manager, Engine engine, Camera camera, Skin skin) {
        super(manager, engine, camera, skin);
    }

    @Override
    public Button getControlButton() {
        return null;
    }
}
