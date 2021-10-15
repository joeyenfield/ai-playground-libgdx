package com.emptypocketstudios.boardgame.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.library.input.NamedInputMultiplexer;
import com.emptypocketstudios.boardgame.library.ui.Scene2DToolkit;
import com.emptypocketstudios.boardgame.ui.controllers.EngineRenderControlOverlay;
import com.emptypocketstudios.boardgame.ui.controllers.MapDesignOverlay;
import com.emptypocketstudios.boardgame.ui.controllers.PathFindingControlsOverlay;

public class EngineControllerManager extends Table {
    int minTouchSizeCm = 50;
    Table innerLayout;
    public NamedInputMultiplexer inputMultiplexer;


    public PathFindingControlsOverlay pathfindingControls;
    public MapDesignOverlay mapDesignerOverlay;
    public EngineRenderControlOverlay renderControls;

    public EngineControllerManager(Engine engine, Camera camera) {
        inputMultiplexer = new NamedInputMultiplexer();
        pathfindingControls = new PathFindingControlsOverlay(this, engine, camera, Scene2DToolkit.skin());
        inputMultiplexer.addProcessor(new GestureDetector(pathfindingControls), "PathFinder-GST");
        inputMultiplexer.addProcessor(pathfindingControls, "PathFinder-CONT");

        mapDesignerOverlay = new MapDesignOverlay(this, engine, camera, Scene2DToolkit.skin());
        inputMultiplexer.addProcessor(new GestureDetector(mapDesignerOverlay), "MapDesign-GST");
        inputMultiplexer.addProcessor(mapDesignerOverlay, "MapDesign-CONT");

        renderControls = new EngineRenderControlOverlay(this, engine, camera, Scene2DToolkit.skin());
        layoutGui();
    }

    public void layoutGui() {
        innerLayout = new Table();
        resize(1, 1);
    }

    public void resize(int width, int height) {
        innerLayout.clear();
        innerLayout.row();
        innerLayout.add(renderControls.getControlButton());
        innerLayout.add().fillX().expandX();
        innerLayout.add();

        innerLayout.row();
        innerLayout.row();
        innerLayout.add().fillY().expandY();
        innerLayout.add().fill().expand();
        innerLayout.add().fillY().expandY();


        innerLayout.row();
        innerLayout.add(mapDesignerOverlay.getControlButton()).size(minTouchSizeCm, minTouchSizeCm);
        innerLayout.add().expandX().fillX();
        innerLayout.add(pathfindingControls.getControlButton()).size(minTouchSizeCm, minTouchSizeCm);

        innerLayout.setFillParent(true);
        clear();
        setFillParent(true);
        add(innerLayout).fill().expand();
    }

    public void updateLogic(float delta) {
//        pathfindingControls.update(delta);
        renderControls.update(delta);
    }
}
