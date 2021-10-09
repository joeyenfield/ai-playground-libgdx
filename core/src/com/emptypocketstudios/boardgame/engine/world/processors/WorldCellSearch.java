package com.emptypocketstudios.boardgame.engine.world.processors;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;
import com.emptypocketstudios.boardgame.engine.world.World;

public class WorldCellSearch {

    World world;

    public WorldCellSearch(World world) {
        this.world = world;
    }

    public Cell spiralSearch(Cell cell, float maxDistance, CellType cellType) {
        return spiralSearch(cell.cellId.x, cell.cellId.y, maxDistance, cellType);
    }

    public Cell spiralSearch(int cX, int cY, float rangeWorldDistance, CellType cellType) {
        int di = 1;
        int dj = 0;
        int segment_length = 1;
        int i = cX - 1;
        int j = cY;
        int segment_passed = 0;
        int maxCellDst2 = MathUtils.ceil(rangeWorldDistance / world.engine.config.cellSize);
        maxCellDst2 = Math.min(maxCellDst2, world.chunksX * world.cellsPerChunkX);
        maxCellDst2 = maxCellDst2 * maxCellDst2;
        while (true) {
            i += di;
            j += dj;
            ++segment_passed;
            int dx = i - cX;
            int dy = j - cY;
            if (dx * dx + dy * dy > maxCellDst2) {
                return null;
            }
            Cell currentCell = world.getCellByCellId(i, j);
            if (currentCell != null && currentCell.type == cellType) {
                return currentCell;
            }
            if (segment_passed == segment_length) {
                segment_passed = 0;
                int buffer = di;
                di = -dj;
                dj = buffer;
                if (dj == 0) {
                    ++segment_length;
                }
            }
        }
    }

    public Cell randomSearchScan(Cell cell, float rangeWorldDistance, CellType type) {
        return randomSearchScan(cell.pos.x, cell.pos.y, rangeWorldDistance, type, 1, new Array<Cell>());
    }

    public Cell randomSearchScan(float pX, float pY, float rangeWorldDistance, CellType type, int count, Array<Cell> result) {
        Array<Cell> cells = new Array<>(2048);
        //Find all of type
        Vector2 min = new Vector2(pX - rangeWorldDistance, pY - rangeWorldDistance);
        Vector2 max = new Vector2(pX + rangeWorldDistance, pY + rangeWorldDistance);

        GridPoint2 start = new GridPoint2();
        GridPoint2 end = new GridPoint2();
        world.getClosestGridPos(min, start);
        world.getClosestGridPos(max, end);
        for (int x = start.x; x < end.x; x++) {
            for (int y = start.y; y < end.y; y++) {
                Cell c = world.getCellByCellId(x, y);
                if (c != null && c.type == type) {
                    cells.add(c);
                }
            }
        }

        if (cells.size > 0) {
            if (count == 1) {
                result.add(this.world.engine.random.random(cells));
                return result.get(0);
            }

            for (int i = 0; i <= count; i++) {
                result.add(this.world.engine.random.random(cells));
            }
        }
        return null;
    }

    public Cell randomSearchMontiCarlo(Cell cell, float rangeWorldDistance, CellType type) {
        return randomSearchMontiCarlo(cell.pos.x, cell.pos.y, rangeWorldDistance, type, 1, 100, new Array<Cell>());
    }

    public Cell randomSearchMontiCarlo(Cell cell, float rangeWorldDistance, int maxAttempts, CellType type) {
        return randomSearchMontiCarlo(cell.pos.x, cell.pos.y, rangeWorldDistance, type, 1, maxAttempts, new Array<Cell>());
    }

    public Cell randomSearchMontiCarlo(float pX, float pY, float rangeWorldDistance, CellType type, int count, Array<Cell> result) {
        return randomSearchMontiCarlo(pX, pY, rangeWorldDistance, type, count, 100 * count, result);
    }

    public Cell randomSearchMontiCarlo(float pX, float pY, float rangeWorldDistance, CellType type, int count, int maxAttempts, Array<Cell> result) {
        Array<Cell> cells = new Array<>(2048);
        //Find all of type
        Vector2 min = new Vector2(pX - rangeWorldDistance, pY - rangeWorldDistance);
        Vector2 max = new Vector2(pX + rangeWorldDistance, pY + rangeWorldDistance);

        GridPoint2 start = new GridPoint2();
        GridPoint2 end = new GridPoint2();
        world.getClosestGridPos(min, start);
        world.getClosestGridPos(max, end);

        int run = 0;
        while (run++ < maxAttempts && cells.size < count) {
            int tX = world.engine.random.random(start.x, end.x);
            int tY = world.engine.random.random(start.y, end.y);

            Cell c = world.getCellByCellId(tX, tY);
            if (c != null && c.type == type) {
                if (!cells.contains(c, true)) {
                    cells.add(c);
                }
            }
        }
        if (cells.size > 0) {
            if (count == 1) {
                result.add(this.world.engine.random.random(cells));
                return result.get(0);
            }

            for (int i = 0; i <= count; i++) {
                result.add(this.world.engine.random.random(cells));
            }
        }
        return null;
    }

}
