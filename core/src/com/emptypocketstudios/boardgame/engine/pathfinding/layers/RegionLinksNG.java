package com.emptypocketstudios.boardgame.engine.pathfinding.layers;

import com.badlogic.gdx.utils.Disposable;
import com.emptypocketstudios.boardgame.engine.world.WorldChunkRegionNodeLink;

import java.util.Objects;

public class RegionLinksNG implements Comparable<RegionLinksNG>, Disposable {
    public RegionLinksNG parent;
    public WorldChunkRegionNodeLink chunkLink;
    public float weight = 1;
    public float travelWeight = 0;
    public float destWeight = 0;

    @Override
    public String toString() {
        return "RL{ " +
                "c=" + chunkLink +
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
        return chunkLink.equals(that.chunkLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunkLink);
    }

    @Override
    public int compareTo(RegionLinksNG regionLinks) {
        return -Float.compare(this.weight, regionLinks.weight);
    }

    @Override
    public void dispose() {
        chunkLink = null;
        parent = null;
        weight = 1;
    }
}
