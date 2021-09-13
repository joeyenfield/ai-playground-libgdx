package com.emptypocketstudios.boardgame.ui.controllers;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellTypes;
import com.emptypocketstudios.boardgame.ui.EngineControllerManager;

public class MapDesignOverlay extends EngineController {
    int minTouchSizeCm = 50;

    boolean enabled = false;
    short currentCellType = CellTypes.GRASS;

    Button enabledButton;
    Button drawButton;
    SelectBox<String> cellType;
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
        cellType = new SelectBox<String>(skin);
        cellType.setItems(CellTypes.names());
        cellType.setSelected(CellTypes.name(CellTypes.GRASS));
        currentCellType = (CellTypes.getId(cellType.getSelected()));

        Table buttonHolder = new Table();
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
                currentCellType = (CellTypes.getId(cellType.getSelected()));
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
                c.type = this.currentCellType;
                c.lastChangeTime = System.currentTimeMillis();
                engine.world.getChunkByWorldPosition(pos).updateRegionsRequired = true;
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
