package com.emptypocketstudios.boardgame.engine.pathfinding.cells;

import com.badlogic.gdx.utils.Disposable;
import com.emptypocketstudios.boardgame.engine.world.Cell;

public class CellLink implements Disposable, Comparable<CellLink> {
    public CellLink parent;
    public Cell originCell;
    public Cell currentCell;
    public float weight = 0;
    public float travelWeight = 0;
    public float destinationWeight = 0;

    public void link(CellLink parent, Cell c1, Cell c2, float travelWeight, float destinationWeight) {
        this.parent = parent;
        this.originCell = c1;
        this.currentCell = c2;
        this.travelWeight = travelWeight;
        this.destinationWeight = destinationWeight;
        this.weight = travelWeight+destinationWeight;
    }

    @Override
    public String toString() {
        return "Path{" +
                "  startCell=" + originCell +
                ", destCell=" + currentCell +
                ", weight=" + weight +
                ", travelWeight=" + travelWeight +
                ", destinationWeight=" + destinationWeight +
                ", \n    parent=" + parent +
                '}';
    }

    @Override
    public void dispose() {
        parent = null;
        originCell = null;
        currentCell = null;
        weight=0;
        travelWeight = 0;
        destinationWeight = 0;
    }

    @Override
    public int compareTo(CellLink path) {
        return Float.compare(weight, path.weight);
    }

}
