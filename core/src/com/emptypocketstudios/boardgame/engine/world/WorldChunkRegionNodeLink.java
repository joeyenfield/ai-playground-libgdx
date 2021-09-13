package com.emptypocketstudios.boardgame.engine.world;

import com.emptypocketstudios.boardgame.engine.pathfinding.layers.RegionLinks;

public class WorldChunkRegionNodeLink {
    public RegionNode source = new RegionNode();
    public RegionNode current = new RegionNode();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldChunkRegionNodeLink that = (WorldChunkRegionNodeLink) o;
        return source.equals(that.source) && current.equals(that.current);
    }

    @Override
    public String toString() {
        return "CnkReLnk{" +
                " source=" + source +
                ", current=" + current +
                '}';
    }
}
