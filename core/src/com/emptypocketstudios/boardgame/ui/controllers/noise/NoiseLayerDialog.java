package com.emptypocketstudios.boardgame.ui.controllers.noise;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.emptypocketstudios.boardgame.library.noise.NoiseLayer;
import com.emptypocketstudios.boardgame.library.ui.Scene2DToolkit;

public class NoiseLayerDialog extends Window {

    NoiseLayer noiseLayer;
    TextButton looseFocus;
    TextButton closeButton;


    String name;

    Table content;

    NoiseLayerPanel noiseLayerPanel;

    public NoiseLayerDialog(NoiseLayer noiseLayer, String name) {
        super(name, Scene2DToolkit.skin());
        this.noiseLayer = noiseLayer;
        this.name = name;
        this.noiseLayerPanel = new NoiseLayerPanel(noiseLayer, name);
        setupGui(Scene2DToolkit.skin());
    }

    public void setupGui(Skin skin) {
        content = new Table();
        content.add(noiseLayerPanel);
        looseFocus = new TextButton("looseFocus", skin);
        content.row();
        content.add(looseFocus).colspan(1);

        add(content).pad(10).fill().expand();
        pack();
        closeButton = new TextButton("X", skin);
        getTitleTable().add(closeButton).size(30, 30).padRight(-10).padTop(0);
        getTitleTable().setHeight(30);

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                noiseLayerPanel.looseFocus();
                setVisible(false);
            }
        });

        looseFocus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                noiseLayerPanel.looseFocus();
            }
        });

    }

    public void writeValues() {
        noiseLayerPanel.writeValues();
    }
}
