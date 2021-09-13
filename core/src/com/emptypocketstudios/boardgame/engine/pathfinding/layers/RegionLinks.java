package com.emptypocketstudios.boardgame.engine.pathfinding.layers;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;

import java.util.Objects;

public class RegionLinks implements Comparable<RegionLinks>, Disposable {
    RegionLinks parent;
    public RegionNode source;
    public RegionNode current;

    public float weight = 1;
    public float travelWeight = 0;
    public float destWeight = 0;

    @Override
    public String toString() {
        return "RegionLinks{" +
                "c=" + current +
                ", s=" + source +
                ", w=" + weight +
                ", t=" + travelWeight +
                ", d=" + destWeight +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionLinks that = (RegionLinks) o;
        return source.equals(that.source) && current.equals(that.current);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, current);
    }

    @Override
    public int compareTo(RegionLinks regionLinks) {
        return Float.compare(this.weight, regionLinks.weight);
    }

    @Override
    public void dispose() {
        Pools.free(source);
        Pools.free(current);
        source = null;
        current = null;
        parent = null;
    }
}
