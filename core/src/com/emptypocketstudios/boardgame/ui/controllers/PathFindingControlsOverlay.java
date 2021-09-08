package com.emptypocketstudios.boardgame.ui.controllers;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.PathFollowComponent;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.ui.EngineControllerManager;

public class PathFindingControlsOverlay extends EngineController {

    boolean enabled = false;

    public Entity selectedEntity = null;
    public Cell selectedCell = null;

    Button pathFinderButton;
    Window controlDialog;


    Button setCellButton;
    Button clearCellButton;

    Button setEntityButton;
    Button clearEntityButton;

    Button displayDebug;
    Button diagonalsEnabled;
    Button distEnabled;


    Button liveUpdateButton;
    Button calculateButton;


//    boolean showDebug =false;
//    boolean startMode = false;
//    boolean endMode = false;
//    boolean enabled = false;
//
//    boolean drawing = false;
//
//    boolean distCheckFast = false;
//    boolean realTimeUpdate = false;
//    boolean diagonal = false;


    public PathFindingControlsOverlay(EngineControllerManager manager, Engine engine, Camera camera, Skin skin) {
        super(manager, engine, camera, skin);
        setupGUI(skin);
    }

    @Override
    public Button getControlButton() {
        return pathFinderButton;
    }

    public void setupGUI(Skin skin) {
        pathFinderButton = new TextButton("Path", skin, "toggle");

        setCellButton = new TextButton("Set Cell", skin, "toggle");
        clearCellButton = new TextButton("Clear Cell", skin);

        setEntityButton = new TextButton("Set Entity", skin, "toggle");
        clearEntityButton = new TextButton("Clear Entity", skin);

        distEnabled = new TextButton("Fast Dist", skin, "toggle");
        diagonalsEnabled = new TextButton("Diagonals", skin, "toggle");
        displayDebug = new TextButton("Debug", skin, "toggle");
        liveUpdateButton = new TextButton("Live", skin, "toggle");
        calculateButton = new TextButton("Calculate", skin);

        Table buttonHolder = new Table();
        buttonHolder.row();
        buttonHolder.add(setCellButton).fillX();
        buttonHolder.add(clearCellButton).fillX();

        buttonHolder.row();
        buttonHolder.add(setEntityButton).fillX();
        buttonHolder.add(clearEntityButton).fillX();

        buttonHolder.row();
        buttonHolder.add(liveUpdateButton).colspan(2).fillX();

        buttonHolder.row();
        buttonHolder.add(calculateButton).colspan(2).fillX();
        buttonHolder.row();
        buttonHolder.add(displayDebug).colspan(2).fillX();
        buttonHolder.row();
        buttonHolder.add(diagonalsEnabled).fillX();
        buttonHolder.add(distEnabled).fillX();

        controlDialog = new Window("Path Finder Helper", skin);
        controlDialog.add(buttonHolder).pad(10).fill().expand();
        controlDialog.setVisible(false);
        controlDialog.pack();

        pathFinderButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (pathFinderButton.isChecked()) {
                    flowIn();
                } else {
                    flowOut();
                }
                updateEnabled();
            }
        });

        setCellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateEnabled();
                super.clicked(event, x, y);
            }
        });

        clearCellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedCell = null;
                super.clicked(event, x, y);
            }
        });

        clearEntityButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedEntity = null;
                super.clicked(event, x, y);
            }
        });
        ClickListener singleSelection = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Actor sourceButton = event.getListenerActor();
                disableIfNotSelected(sourceButton, setCellButton);
                disableIfNotSelected(sourceButton, setEntityButton);
                updateEnabled();
                super.clicked(event, x, y);
            }

            private void disableIfNotSelected(Actor act, Button button) {
                if (act != button) {
                    button.setChecked(false);
                }
            }
        };
        setCellButton.addListener(singleSelection);
        setEntityButton.addListener(singleSelection);
        calculateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateTarget();
                super.clicked(event, x, y);
            }
        });
    }

    public void flowIn() {
        if (!manager.getStage().getActors().contains(controlDialog, true)) {
            manager.getStage().addActor(controlDialog);
        }
        controlDialog.setVisible(true);
    }

    public void flowOut() {
        controlDialog.setVisible(false);
    }

    public void updateEnabled() {
        enabled = pathFinderButton.isChecked() &&
                (setCellButton.isChecked()
                        || setEntityButton.isChecked());
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!enabled) return false;
        pos.x = screenX;
        pos.y = screenY;
        helper.screenToWorld(camera, pos);
        if (setCellButton.isChecked()) {
            Cell c = engine.world.getCellAtWorldPosition(pos);
            return processCellSelection(c);
        }

        return false;
    }

    public void updateTarget() {
        if (selectedEntity != null && selectedCell != null) {
            selectedEntity.getEntityComponent(PathFollowComponent.class).travelTo(selectedCell,
                    diagonalsEnabled.isChecked(),
                    distEnabled.isChecked(),
                    displayDebug.isChecked()
            );
        }
    }

    public boolean processCellSelection(Cell c) {
        if (c != null) {
            if (setCellButton.isChecked()) {
                selectedCell = c;
                if (liveUpdateButton.isChecked()) {
                    updateTarget();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!enabled) return false;
        pos.x = screenX;
        pos.y = screenY;
        helper.screenToWorld(camera, pos);
        if (setCellButton.isChecked()) {
            Cell c = engine.world.getCellAtWorldPosition(pos);
            return processCellSelection(c);
        }
        if (setEntityButton.isChecked()) {
            selectedEntity = engine.getEntityNearPos(pos);
            if (liveUpdateButton.isChecked()) {
                updateTarget();
            }
            System.out.println("Entity Found : " + selectedEntity);
        }

        return false;
    }
}
