package com.emptypocketstudios.boardgame.engine.world;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Cell implements Pool.Poolable {
    public float resources = 1;
    public short type = CellTypes.GRASS;
    public RegionNode region = new RegionNode();
    public GridPoint2 cellId = new GridPoint2();
    public Vector2 pos = new Vector2();
    public Rectangle boundary = new Rectangle();
    public long lastChangeTime = 0;

    public Cell[][] links = new Cell[3][3];

    public void init(GridPoint2 chunkId, GridPoint2 cellId, Rectangle region, short cellType) {
        this.type = cellType;
        this.boundary.set(region);
        this.region.chunkId.set(chunkId);
        this.cellId.set(cellId);
        pos.x = this.boundary.x + this.boundary.width / 2;
        pos.y = this.boundary.y + this.boundary.height / 2;
        this.resources = 1;
        this.lastChangeTime = System.currentTimeMillis();
    }

    public Cell getLink(int dx, int dy) {
        return links[1 + dx][1 + dy];
    }

    public boolean canMove(int dx, int dy) {
        boolean canMove = false;

        if (dx * dy == 0) {
            // Up / Down / Left / Right
            Cell c = links[1 + dx][1 + dy];
            if (c != null) {
                canMove = !CellTypes.isBlocked(c.type);
            }
        } else {
            // Diagonals
            //     Y   Z
            //     | /
            //     C -- X
            // Can only move to Z if Z is free and so is X OR Y
            Cell z = links[1 + dx][1 + dy];
            Cell x = links[1 + dx][1];
            Cell y = links[1][1 + dy];

            if (z != null && !CellTypes.isBlocked(z.type)) {
                canMove = (x != null && !CellTypes.isBlocked(x.type)) || (y != null && !CellTypes.isBlocked(y.type));
            }
        }

        return canMove;
    }

    public void link(int dx, int dy, Cell cell) {
        if (cell == null) return;
        //Stops infinite loop to only happen once
        links[1 + dx][1 + dy] = cell;
        if (cell.links[1 - dx][1 - dy] != this) {
            cell.link(-dx, -dy, this);
        }
    }

    public void unlink(int dx, int dy) {
        //Stops infinite loop to only happen once
        Cell cell = links[1 + dx][1 + dy];
        links[1 + dx][1 + dy] = null;
        if (cell != null && cell.links[1 - dx][1 - dy] != null) {
            cell.link(-dx, -dy, cell);
        }
    }

    public void unlinkAll() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                unlink(x, y);
            }
        }
    }

    public String toString() {
        return "Cell[" + cellId.x + "," + cellId.y + "]";
    }

    @Override
    public void reset() {
        unlinkAll();
    }

    public boolean isLinkCellType(int dx, int dy, short cellType) {
        boolean result = false;
        Cell c = getLink(dx, dy);
        result = c != null && c.type == cellType;
        return result;
    }

    public boolean containsWorldPos(Vector2 pos) {
        return boundary.contains(pos);
    }
}
