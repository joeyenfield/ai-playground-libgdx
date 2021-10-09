package com.emptypocketstudios.boardgame.ui.controllers.noise;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.emptypocketstudios.boardgame.library.noise.NoiseLayer;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.spinner.FloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

public class NoiseLayerPanel extends VisTable {
    NoiseLayer noiseLayer;
    VisSlider weight = null;
    Spinner octavesX = null;
    Spinner octavesY = null;
    Spinner dx = null;
    Spinner dy = null;
    Spinner power = null;
    VisSlider minThreshold = null;
    VisSlider maxThreshold = null;
    VisCheckBox additive = null;
    VisTable content;

    public NoiseLayerPanel(NoiseLayer noiseLayer, String name) {
        this.noiseLayer = noiseLayer;
        setupGui();
    }

    public void looseFocus() {
        getStage().setScrollFocus(null);
        getStage().setKeyboardFocus(null);
    }

    public void setupGui() {
        content = new VisTable();

        weight = slider("weight", 1, -2, 2);
        octavesX = spinner("octavesX", 1, 1, 9999999, 1);
        octavesY = spinner("octavesX", 1, 1, 9999999, 1);
        dx = spinner("dx", 0, 0, 9999999, 0.01f);
        dy = spinner("dy", 0, 0, 9999999, 0.01f);
        power = spinner("power", 1, -999999, 9999999, 0.1f);
        minThreshold = slider("minThreshold", 0, 0, 1);
        maxThreshold = slider("maxThreshold", 1, 0, 1);
        additive = checkbox("Additive", true);

        minThreshold.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (minThreshold.getValue() > maxThreshold.getValue()) {
                    maxThreshold.setValue(minThreshold.getValue());
                }
            }
        });

        maxThreshold.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (maxThreshold.getValue() < minThreshold.getValue()) {
                    minThreshold.setValue(maxThreshold.getValue());
                }
            }
        });

        add(content).pad(10).fill().expand();
    }

    public float getValue(String value, float defaultValue) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Spinner spinner(String name, float current, float min, float max, float step) {
        FloatSpinnerModel model = new FloatSpinnerModel("" + current, "" + min, "" + max, "" + step);
        Spinner spinner = new Spinner(null, model);

        content.row();
        content.add(new VisLabel(name));
        content.add(spinner).align(Align.left);
        return spinner;
    }

    public VisSlider slider(String name, float current, float min, float max) {
        VisSlider slider = new VisSlider(min, max, (max - min) / 100f, false);
        slider.setValue(current);
        content.row();
        content.add(new VisLabel(name));
        content.add(slider);
        return slider;
    }

    public VisCheckBox checkbox(String name, boolean checked) {
        VisCheckBox slider = new VisCheckBox(null, checked);
        content.row();
        content.add(new VisLabel(name));
        content.add(slider);
        return slider;
    }

    public void writeValues() {
        noiseLayer.weight = weight.getValue();
        noiseLayer.octavesX = ((FloatSpinnerModel) octavesX.getModel()).getValue().floatValue();
        noiseLayer.octavesY = ((FloatSpinnerModel) octavesY.getModel()).getValue().floatValue();
        noiseLayer.dx = ((FloatSpinnerModel) dx.getModel()).getValue().floatValue();
        noiseLayer.dy = ((FloatSpinnerModel) dy.getModel()).getValue().floatValue();
        noiseLayer.power = ((FloatSpinnerModel) power.getModel()).getValue().floatValue();
        noiseLayer.minThreshold = minThreshold.getValue();
        noiseLayer.maxThreshold = maxThreshold.getValue();
        noiseLayer.additive = additive.isChecked();
    }
}
