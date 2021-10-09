package com.emptypocketstudios.boardgame.engine.world;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Cell implements Pool.Poolable {

    public CellType type = CellType.GRASS;
    public int typeVariant = 0;
    public long lastChangeTime = 0;

    public RegionNode region = new RegionNode();
    public GridPoint2 cellId = new GridPoint2();

    public Vector2 pos = new Vector2();
    public Rectangle boundary = new Rectangle();

    public boolean isRoad = false;
    public Cell[][] links = new Cell[3][3];
    public WorldChunk chunk;

    public void init(GridPoint2 chunkId, GridPoint2 cellId, Rectangle region, CellType cellType) {
        this.type = cellType;
        this.boundary.set(region);
        this.region.chunkId.set(chunkId);
        this.cellId.set(cellId);
        this.isRoad = false;
        pos.x = this.boundary.x + this.boundary.width / 2;
        pos.y = this.boundary.y + this.boundary.height / 2;
        chunk.updateRegionsRequired = true;
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
                canMove = !c.type.blocked;
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

            if (z != null && !z.type.blocked) {
                canMove = (x != null && !x.type.blocked) || (y != null && !y.type.blocked);
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

    public boolean isLinkCellType(int dx, int dy, CellType cellType) {
        boolean result = false;
        Cell c = getLink(dx, dy);
        result = c != null && c.type == cellType;
        return result;
    }

    public boolean isLinkCellRoad(int dx, int dy) {
        boolean result = false;
        Cell c = getLink(dx, dy);
        result = c != null && c.isRoad;
        return result;
    }

    public boolean containsWorldPos(Vector2 pos) {
        return boundary.contains(pos);
    }

    public void setType(CellType newCellType) {
        setType(newCellType, 0, false);
    }

    public void setType(CellType newCellType, int variant) {
        setType(newCellType, variant, false);
    }

    public void setType(CellType newCellType, int variant, boolean road) {
        this.type = newCellType;
        this.typeVariant = variant;
        this.isRoad = road;
        this.chunk.updateRegionsRequired = true;
        this.lastChangeTime = System.currentTimeMillis();
    }

    public void setRoad(boolean road) {
        setType(type, typeVariant, road);
    }
}
