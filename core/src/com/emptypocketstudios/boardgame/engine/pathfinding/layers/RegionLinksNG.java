package com.emptypocketstudios.boardgame.engine.pathfinding.layers;

import com.badlogic.gdx.utils.Pool;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;

import java.util.Objects;

public class RegionLinksNG implements Comparable<RegionLinksNG>, Pool.Poolable {
    public RegionLinksNG parent;
    public RegionNode regionStart;
    public RegionNode regionEnd;
    public float weight = 1;
    public float travelWeight = 0;
    public float destWeight = 0;

    @Override
    public String toString() {
        return "RL{ " +
                "c=" + regionStart +
                "c=" + regionEnd +
                ", w=" + weight +
                ", t=" + travelWeight +
                ", d=" + destWeight +
                " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionLinksNG that = (RegionLinksNG) o;
        return regionStart.equals(that.regionStart) && regionEnd.equals(that.regionEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regionStart, regionEnd);
    }

    @Override
    public int compareTo(RegionLinksNG regionLinks) {
        return Float.compare(this.weight, regionLinks.weight);
    }

    @Override
    public void reset() {
        parent = null;
        regionStart = null;
        regionEnd = null;
    }
}
