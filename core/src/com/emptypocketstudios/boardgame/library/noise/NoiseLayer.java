package com.emptypocketstudios.boardgame.library.noise;

public class NoiseLayer {
    public float weight = 1;
    public float octavesX = 1;
    public float octavesY = 1;
    public float dx = 0;
    public float dy = 0;
    public float power = 1;
    public float minThreshold = 0;
    public float maxThreshold = 1;
    public boolean additive = true;

    @Override
    public String toString() {
        return "NoiseLayer{" +
                "weight=" + weight +
                ", octavesX=" + octavesX +
                ", octavesY=" + octavesY +
                ", dx=" + dx +
                ", dy=" + dy +
                ", power=" + power +
                ", minThreshold=" + minThreshold +
                ", maxThreshold=" + maxThreshold +
                ", additive=" + additive +
                '}';
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getOctavesX() {
        return octavesX;
    }

    public void setOctavesX(float octavesX) {
        this.octavesX = octavesX;
    }

    public float getOctavesY() {
        return octavesY;
    }

    public void setOctavesY(float octavesY) {
        this.octavesY = octavesY;
    }

    public float getDx() {
        return dx;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public float getDy() {
        return dy;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public void setThreshold(float value) {
        this.minThreshold = value;
        this.maxThreshold = value;
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
}