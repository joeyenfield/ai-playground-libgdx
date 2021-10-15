package com.emptypocketstudios.boardgame.ui.controllers;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.ui.EngineControllerManager;
import com.emptypocketstudios.boardgame.ui.render.CellDebugRenderer;
import com.emptypocketstudios.boardgame.ui.render.WorldChunkRenderer;
import com.kotcrab.vis.ui.widget.VisLabel;

public class EngineRenderControlOverlay extends EngineController {
    boolean enabled = false;
    Button enabledButton;
    Window controlDialog;

    TextButton showAxis;
    TextButton showWorldBounds;
    TextButton showCellRegion;
    TextButton showChunkBoundary;
    TextButton showCellBoundary;
    TextButton showCellText;
    TextButton showCellTextures;

    TextField minCellSizeToDisplayTextSpinner;
    TextField minCellSizeToDisplayBoundarySpinner;
    TextField minCellSizeToDisplayRegionSpinner;


    public EngineRenderControlOverlay(EngineControllerManager manager, Engine engine, Camera camera, Skin skin) {
        super(manager, engine, camera, skin);
        setupGUI(skin);
    }

    public void setupGUI(Skin skin) {
        showAxis = new TextButton("Engine Axis", skin, "toggle");
        showWorldBounds = new TextButton("World Bounds", skin, "toggle");
        showChunkBoundary = new TextButton("Chunk Boundary", skin, "toggle");
        showCellRegion = new TextButton("Cell Regions", skin, "toggle");
        showCellText = new TextButton("Cell Text", skin, "toggle");
        showCellBoundary = new TextButton("Cell Boundary", skin, "toggle");
        showCellTextures = new TextButton("Cell Texture", skin, "toggle");

        WorldChunkRenderer chunkRenderer = engine.render.worldRenderer.worldChunkRenderer;
        CellDebugRenderer debugRender = engine.render.worldRenderer.worldChunkRenderer.cellDebugRenderer;

        //Fetch current values
        showAxis.setChecked(engine.render.draw2DAxis);
        showWorldBounds.setChecked(engine.render.worldRenderer.drawWorldBounds);
        showChunkBoundary.setChecked(chunkRenderer.drawChunkBoundary);
        showCellRegion.setChecked(chunkRenderer.drawCellRegions);
        showCellText.setChecked(chunkRenderer.drawCellDebugText);
        showCellBoundary.setChecked(chunkRenderer.drawCellBoundary);
        showCellTextures.setChecked(chunkRenderer.drawCellTexture);
        minCellSizeToDisplayTextSpinner = new TextField("" + (debugRender.minCellSizeToDisplayTextDebug), skin);
        minCellSizeToDisplayBoundarySpinner = new TextField("" + (debugRender.minCellSizeToDisplayBoundaryDebug), skin);
        minCellSizeToDisplayRegionSpinner = new TextField("" + (debugRender.minCellSizeToDisplayRegionDebug), skin);

        Table buttonHolder = new Table();
        buttonHolder.row();
        buttonHolder.add(new VisLabel("World")).colspan(3).fillX().expand();
        buttonHolder.row();
        buttonHolder.add(showWorldBounds).fillX().expand();
        buttonHolder.add(showAxis).fillX().expand();
        buttonHolder.add(showChunkBoundary).fillX().expand();
        buttonHolder.row();
        buttonHolder.add(new VisLabel("Cells")).colspan(3).fillX().expand();
        buttonHolder.row();
        buttonHolder.add(showCellRegion).fillX().expand();
        buttonHolder.add(showCellBoundary).fillX().expand();
        buttonHolder.add(showCellText).fillX().expand();
        buttonHolder.row();
        buttonHolder.add(minCellSizeToDisplayRegionSpinner).fillX().expand();
        buttonHolder.add(minCellSizeToDisplayBoundarySpinner).fillX().expand();
        buttonHolder.add(minCellSizeToDisplayTextSpinner).fillX().expand();
        buttonHolder.row();
        buttonHolder.add(showCellTextures).colspan(3).fillX().expand();
        buttonHolder.row();

        controlDialog = new Window("Page", skin);
        controlDialog.add(buttonHolder).pad(10).fill().expand();
        controlDialog.setVisible(false);
        controlDialog.pack();


        enabledButton = new TextButton("Render", skin, "toggle");
        enabledButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (enabledButton.isChecked()) {
                    flowIn();
                } else {
                    flowOut();
                }
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


    @Override
    public Button getControlButton() {
        return enabledButton;
    }

    public void update(float delta) {
        engine.render.draw2DAxis = this.showAxis.isChecked();
        engine.render.worldRenderer.drawWorldBounds = this.showWorldBounds.isChecked();
        engine.render.worldRenderer.worldChunkRenderer.drawChunkBoundary = this.showChunkBoundary.isChecked();
        engine.render.worldRenderer.worldChunkRenderer.drawCellRegions = showCellRegion.isChecked();
        engine.render.worldRenderer.worldChunkRenderer.drawCellBoundary = showCellBoundary.isChecked();
        engine.render.worldRenderer.worldChunkRenderer.drawCellDebugText = showCellText.isChecked();
        engine.render.worldRenderer.worldChunkRenderer.drawCellTexture = showCellTextures.isChecked();

        CellDebugRenderer debugRender = engine.render.worldRenderer.worldChunkRenderer.cellDebugRenderer;

        debugRender.minCellSizeToDisplayTextDebug = getValue(minCellSizeToDisplayTextSpinner);
        debugRender.minCellSizeToDisplayBoundaryDebug = getValue(minCellSizeToDisplayBoundarySpinner);
        debugRender.minCellSizeToDisplayRegionDebug = getValue(minCellSizeToDisplayRegionSpinner);
    }

    public float getValue(TextField field) {
        try {
            return Float.valueOf(field.getText());
        } catch (Exception e) {
            return 1;
        }
    }
}
