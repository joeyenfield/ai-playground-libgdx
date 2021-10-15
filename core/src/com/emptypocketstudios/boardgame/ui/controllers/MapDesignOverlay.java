package com.emptypocketstudios.boardgame.ui.controllers;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;
import com.emptypocketstudios.boardgame.ui.EngineControllerManager;

public class MapDesignOverlay extends EngineController {
    boolean enabled = false;
    CellType currentCellType = CellType.GRASS;

    Button enabledButton;
    Button drawButton;
    CheckBox isRoadCheckbox;
    SelectBox<String> cellType;
    SelectBox<String> drawMode;
    Window controlDialog;

    public MapDesignOverlay(EngineControllerManager manager, Engine engine, Camera camera, Skin skin) {
        super(manager, engine, camera, skin);
        setupGUI(skin);
    }

    @Override
    public Button getControlButton() {
        return enabledButton;
    }

    public void setupGUI(Skin skin) {
        enabledButton = new TextButton("Map", skin, "toggle");
        drawButton = new TextButton("Draw", skin, "toggle");

        drawMode = new SelectBox<String>(skin);
        drawMode.setItems("ROAD+TYPE", "ROAD", "TYPE"
        );
        drawMode.setSelected("TYPE");
        cellType = new SelectBox<String>(skin);
        cellType.setItems(CellType.names());
        cellType.setSelected(CellType.GRASS.name());
        currentCellType = (CellType.valueOf(cellType.getSelected()));
        isRoadCheckbox = new CheckBox("Is Road", skin);

        Table buttonHolder = new Table();
        buttonHolder.add(drawMode).fillX();
        buttonHolder.row();
        buttonHolder.add(isRoadCheckbox).fillX();
        buttonHolder.row();
        buttonHolder.add(cellType).fillX();
        buttonHolder.row();
        buttonHolder.add(drawButton).fillX();
        buttonHolder.row();

        controlDialog = new Window("Page", skin);
        controlDialog.add(buttonHolder).pad(10).fill().expand();
        controlDialog.setVisible(false);
        controlDialog.pack();

        enabledButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (enabledButton.isChecked()) {
                    flowIn();
                } else {
                    flowOut();
                }
                updateEnabled();
            }
        });

        drawButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateEnabled();
            }
        });

        cellType.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentCellType = (CellType.valueOf(cellType.getSelected()));
            }
        });
    }

    public void updateEnabled() {
        enabled = drawButton.isChecked() && enabledButton.isChecked();
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

    public void update(float delta) {
    }

    public boolean processCellSelection(Cell c) {
        if (c != null) {
            if (enabled) {
                if (drawMode.getSelected().contains("TYPE")) {
                    c.setType(this.currentCellType, 0);
                }
                if (drawMode.getSelected().contains("ROAD")) {
                    c.setRoad(this.isRoadCheckbox.isChecked());
                }
                WorldChunk chunk = engine.world.getChunkAtWorldPosition(pos);
                if (chunk != null) {
                    chunk.updateRegionsRequired = true;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!enabled) return false;
        mapScreenToEngine(screenX, screenY, pos);
        Cell c = engine.world.getCellAtWorldPosition(pos);
        return processCellSelection(c);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!enabled) return false;
        mapScreenToEngine(screenX, screenY, pos);
        Cell c = engine.world.getCellAtWorldPosition(pos);
        return processCellSelection(c);
    }


}
