package com.emptypocketstudios.boardgame.library.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.emptypocketstudios.boardgame.MainGame;
import com.emptypocketstudios.boardgame.library.input.NamedInputMultiplexer;

public abstract class StageScreen extends GameScreen {

    Viewport stageViewport;
    private Stage stage;

    public StageScreen(MainGame mainGame, NamedInputMultiplexer inputMultiplexer) {
        super(mainGame, inputMultiplexer);
        stageViewport = new ExtendViewport(800, 600);
    }

    public abstract void createStage(Stage stage);

    @Override
    public void addInputMultiplexer(NamedInputMultiplexer input) {
        super.addInputMultiplexer(input);
    }

    @Override
    public void removeInputMultiplexer(NamedInputMultiplexer input) {
        super.removeInputMultiplexer(input);
        input.removeProcessor(stage);
    }

    @Override
    public void show() {
        super.show();
        stage = new Stage(stageViewport);
        setStage(stage);
        createStage(stage);
    }

    @Override
    public void hide() {
        super.hide();
        if (getStage() != null) {
            getStage().dispose();
        }
        setStage(null);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stageViewport.update(width, height, true);
    }

    @Override
    public void renderScreen(float delta) {
        super.renderScreen(delta);
        drawStage(delta);
    }

    @Override
    public void updateLogic(float delta) {
        super.updateLogic(delta);
        getStage().act(delta);
    }

    public void drawStage(float delta) {
        try {
            getStage().getViewport().apply();
            getStage().draw();
        } catch (Exception e) {
            if (getStage().getBatch().isDrawing()) {
                //Bug where draw fails which results in the batch.end never being called
                getStage().getBatch().end();
            }
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        if (this.stage != null) {
            parentInputMultiplexer.removeProcessor(this.stage);
        }
        this.stage = stage;
        if (this.stage != null) {
            parentInputMultiplexer.addProcessor(0, this.stage, "STAGE");
        }
    }


}
