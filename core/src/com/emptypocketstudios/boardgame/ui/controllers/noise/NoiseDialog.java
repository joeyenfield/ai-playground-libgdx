package com.emptypocketstudios.boardgame.ui.controllers.noise;


import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.emptypocketstudios.boardgame.library.noise.NoiseGenerator;
import com.emptypocketstudios.boardgame.library.noise.NoiseLayer;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;

import java.util.HashMap;

public class NoiseDialog extends VisWindow {
    VisTable content;
    VisTextButton addLayer;
    VisTextButton debugLayer;
    VisTextButton removeLayer;
    VisTextButton closeButton;
    NoiseGenerator generator;

    VisList<String> leftItems = new VisList<>();
    HashMap<NoiseLayer, NoiseLayerPanel> panels = new HashMap<>();
    HashMap<String, NoiseLayerPanel> nameMap = new HashMap<>();

    VisSlider minThreshold = null;
    VisSlider maxThreshold = null;

    public NoiseDialog(NoiseGenerator generator, String title) {
        super(title);
        this.generator = generator;
        setupGui();
    }

    public VisSlider slider(String name, float current, float max) {
        VisSlider slider = new VisSlider(0, max, max / 100f, false);
        slider.setValue(current);
        row();
        add(new VisTextField(name));
        add(slider);
        return slider;
    }

    public void setupGui() {
        content = new VisTable();

        VisSplitPane split = new VisSplitPane(leftItems, content, false);
        split.setSplitAmount(0.2f);
        row();
        add(split).colspan(2).fill().expand();
        minThreshold = slider("min", 0, 1);
        maxThreshold = slider("max", 1, 1);

        row();
        addLayer = new VisTextButton("Add");
        debugLayer = new VisTextButton("Debug");
        removeLayer = new VisTextButton("Remove");
        add(addLayer);
        add(debugLayer);
        add(removeLayer);
        pack();

        closeButton = new VisTextButton("X");
        getTitleTable().add(closeButton).size(30, 30).padRight(-10).padTop(0);
        getTitleTable().setHeight(30);

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisible(false);
            }
        });

        addLayer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                generator.addLayer();
                updateGUI();
            }
        });

        debugLayer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                NoiseLayerPanel panel = nameMap.get(leftItems.getSelected());
                if (panel != null) {
                    if (generator.getDebugLayer() == panel.noiseLayer) {
                        generator.debugLayer(null);
                    } else {
                        generator.debugLayer(panel.noiseLayer);
                    }
                }
                updateGUI();
            }
        });

        removeLayer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = leftItems.getSelected();
                NoiseLayerPanel panel = nameMap.get(name);
                if (panel != null) {
                    generator.debugLayer(null);
                    generator.removeLayer(panel.noiseLayer);
                    nameMap.remove(name);
                    panels.remove(panel.noiseLayer);
                }
                leftItems.getItems().removeValue(name, true);
                leftItems.setSelected(null);
                updateGUI();
            }
        });

    }

    public void writeValues() {
        for (int i = 0; i < generator.getLayerCount(); i++) {
            panels.get(generator.getLayer(i)).writeValues();
        }
        generator.setMinThreshold(minThreshold.getValue());
        generator.setMaxThreshold(maxThreshold.getValue());
    }

    public void updateGUI() {
        content.clearChildren();
        for (int i = 0; i < generator.getLayerCount(); i++) {
            NoiseLayer layer = generator.getLayer(i);
            String name = "Layer : " + (i + 1);
            NoiseLayerPanel dialog = initPanel(layer, name);
            if (!leftItems.getItems().contains(name, false)) {
                leftItems.getItems().add(name);
                leftItems.setSelected(name);
            }
        }
        pack();
    }

    public void update() {
        String name = leftItems.getSelected();
        if (name != null && name.length() > 0) {
            NoiseLayerPanel panel = getPanelByName(name);
            if (panel != null) {
                if (generator.getDebugLayer() != panel.noiseLayer) {
                    generator.debugLayer(null);
                }
                content.clear();
                content.add(panel).expand().fill();
            }
        }
        pack();
        writeValues();
    }

    public NoiseLayerPanel getPanelByName(String name) {
        return nameMap.get(name);
    }

    public NoiseLayerPanel initPanel(NoiseLayer layer, String name) {
        if (!panels.containsKey(layer)) {
            NoiseLayerPanel dialog = new NoiseLayerPanel(layer, name);
            nameMap.put(name, dialog);
            panels.put(layer, dialog);
        }
        return panels.get(layer);
    }
}
