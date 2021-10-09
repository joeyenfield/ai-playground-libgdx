package com.emptypocketstudios.boardgame.engine.world;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Disposable;

import java.util.Objects;

public class RegionNode implements Disposable {
    public GridPoint2 chunkId = new GridPoint2();
    public int regionId = 0;
    public float regionWalkWeight = 0;

    public void set(Cell c) {
        set(c.region);
    }

    public RegionNode set(RegionNode c) {
        chunkId.set(c.chunkId);
        regionId = c.regionId;
        regionWalkWeight = c.regionWalkWeight;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionNode that = (RegionNode) o;
        return regionId == that.regionId && chunkId.equals(that.chunkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunkId, regionId);
    }

    @Override
    public String toString() {
        return "{" +
                "c=" + chunkId +
                ",r=" + regionId +
                '}';
    }

    public void set(int x, int y, int regionId) {
        this.chunkId.x = x;
        this.chunkId.y = y;
        this.regionId = regionId;
    }

    public boolean contains(Cell c) {
        if (c == null) return false;
        return this.equals(c.region);
    }

    public float dst2(RegionNode current) {
        float dx = current.chunkId.x - chunkId.x;
        float dy = current.chunkId.y - chunkId.y;
        return dx * dx + dy * dy;
    }

    @Override
    public void dispose() {
        chunkId.set(0, 0);
        regionWalkWeight = 1f;
    }
}
