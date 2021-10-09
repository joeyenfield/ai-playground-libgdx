package com.emptypocketstudios.boardgame.library.noise;

import com.badlogic.gdx.utils.Array;

public class NoiseGenerator {
    float minThreshold = 0;
    float maxThreshold = 1;
    Array<NoiseLayer> layers = new Array<>();
    OpenSimplexNoiseFloat noise = new OpenSimplexNoiseFloat();

    NoiseLayer debugLayer = null;

    public void addLayer(float x, float y, float octavesX, float octavesY, float weight, float power) {
        NoiseLayer l = new NoiseLayer();
        l.dx = x;
        l.dy = y;
        l.octavesX = octavesX;
        l.octavesY = octavesY;
        l.weight = weight;
        l.power = power;
        layers.add(l);
    }

    public NoiseLayer addLayer() {
        NoiseLayer l = new NoiseLayer();
        layers.add(l);
        return l;
    }

    public void debugLayer(NoiseLayer debugLayer) {
        this.debugLayer = debugLayer;
    }

    public NoiseLayer getDebugLayer() {
        return this.debugLayer;
    }

    public int getLayerCount() {
        return layers.size;
    }

    public NoiseLayer getLayer(int i) {
        return layers.get(i);
    }

    public float getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(float minThreshold) {
        this.minThreshold = minThreshold;
    }

    public float getMaxThreshold() {
        return maxThreshold;
    }

    public void setMaxThreshold(float maxThreshold) {
        this.maxThreshold = maxThreshold;
    }

    public float getValue(float x, float y) {
        return getValue(x, y, false);
    }

    public float getValue(float x, float y, boolean debug) {
        float totalWeight = 0;
        float value = 0;
        float px = 0;
        float py = 0;
        for (int i = 0; i < layers.size; i++) {
            NoiseLayer layer = layers.get(i);
            if (debug) System.out.println("Layer[" + (i + 1) + "] - " + layer.toString());
            px = (layer.dx + x) * layer.octavesX;
            py = (layer.dy + y) * layer.octavesY;

            float noiseValue = (float) ((1 + noise.eval(px, py)) / 2);
            if (debug) System.out.println("Noise : " + noiseValue);
            noiseValue = (float) Math.pow(noiseValue, layer.power);
            if (debug) System.out.println(" Pow: " + noiseValue);
            if (noiseValue < layer.minThreshold) {
                noiseValue = 0;
            } else if (noiseValue > layer.maxThreshold) {
                noiseValue = 1;
            }

            if (layer == debugLayer) {
                return noiseValue;
            }
            if (debug) System.out.println("  TH: " + noiseValue);
            if (layer.additive) {
                value += noiseValue * layer.weight;
                totalWeight += layer.weight;
            } else {
                value *= noiseValue * layer.weight;
                totalWeight *= layer.weight;
            }
            if (debug) System.out.println("Value: " + value);
            if (debug) System.out.println("TotalWeight: " + totalWeight);
        }
        value = value / totalWeight;
        if (debug) System.out.println("Scaled Value: " + value);
        if (value < minThreshold) {
            value = 0;
        } else if (value > maxThreshold) {
            value = 1;
        }
        if (debug) System.out.println("FinalValue: " + value);
        return value;
    }

    public void removeLayer(NoiseLayer noiseLayer) {
        layers.removeValue(noiseLayer, true);
    }
}
