package com.emptypocketstudios.boardgame.engine.world;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.emptypocketstudios.boardgame.engine.Engine;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.world.processors.WorldCellSearch;
import com.emptypocketstudios.boardgame.engine.world.processors.WorldEntitySearch;
import com.emptypocketstudios.boardgame.engine.world.processors.WorldLoadingProcessor;
import com.emptypocketstudios.boardgame.library.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class World {
    public Engine engine;
    public int chunksX;
    public int chunksY;
    public int cellsPerChunkX;
    public int cellsPerChunkY;

    public float cellSizeX;
    public float cellSizeY;

    public Rectangle boundary = new Rectangle();
    public WorldChunk[][] chunks;

    public HashMap<RegionNode, Array<RegionNode>> regionLinks = new HashMap<>();
    public HashMap<RegionNode, Array<RegionNode>> diagonalRegionLinks = new HashMap<>();

    public Map<String, Entity> namedEntities = new HashMap<>();

    public WorldCellSearch cellSearcher;
    public WorldEntitySearch entitySearcher;

    public WorldLoadingProcessor worldLoadingProcessor;

    public World(Rectangle boundary, int chunksX, int chunksY, int cellsPerChunkX, int cellsPerChunkY) {
        this.chunksX = chunksX;
        this.chunksY = chunksY;
        this.cellsPerChunkX = cellsPerChunkX;
        this.cellsPerChunkY = cellsPerChunkY;
        this.boundary.set(boundary);
        this.chunks = new WorldChunk[chunksX][chunksY];
        this.cellSizeX = this.boundary.width / (this.chunksX * this.cellsPerChunkX);
        this.cellSizeY = this.boundary.height / (this.chunksY * this.cellsPerChunkY);
        this.cellSearcher = new WorldCellSearch(this);
        this.entitySearcher = new WorldEntitySearch(this);
        this.worldLoadingProcessor = new WorldLoadingProcessor(this);
    }

    public void printRegionLinks(){
        System.out.println("Region Links : ");
        for (Map.Entry<RegionNode, Array<RegionNode>> entry : regionLinks.entrySet()) {
            System.out.println("     "+entry.getKey() + " : [" + entry.getValue().toString(",") + "]");
        }

        System.out.println("Diagonal Region Links : ");
        for (Map.Entry<RegionNode, Array<RegionNode>> entry : diagonalRegionLinks.entrySet()) {
            System.out.println("     "+entry.getKey() + " : [" + entry.getValue().toString(",") + "]");
        }
    }
    public void addEntity(Entity e) {
        e.world = this;
        namedEntities.put(e.name, e);
        engine.postOffice.register(e.name, e);
        WorldChunk chunk = getChunkAtWorldPosition(e.pos);
        if (chunk != null) {
            chunk.addEntity(e);
        }
    }

    public WorldChunk getChunkAtWorldPosition(Vector2 pos) {
        return getChunkAtWorldPosition(pos.x, pos.y, false);
    }

    public WorldChunk getChunkAtWorldPosition(float x, float y) {
        return getChunkAtWorldPosition(x, y, false);
    }

    public WorldChunk getChunkAtWorldPosition(float x, float y, boolean bounded) {
        float dx = (x - boundary.x) / boundary.width;
        float dy = (y - boundary.y) / boundary.height;
        int xId = (int) MathUtils.floor(dx * (chunksX));
        int yId = (int) MathUtils.floor(dy * (chunksY));
        if (bounded) {
            xId = MathUtils.clamp(xId, 0, chunksX - 1);
            yId = MathUtils.clamp(yId, 0, chunksY - 1);
        }
        return getChunkByChunkId(xId, yId);

    }

    public void getClosestGridPos(Vector2 pos, GridPoint2 point) {
        point.x = (int) (MathUtils.clamp((pos.x - boundary.x) / boundary.width, 0f, 1f) * chunksX * cellsPerChunkX);
        point.y = (int) (MathUtils.clamp((pos.y - boundary.y) / boundary.height, 0f, 1f) * chunksY * cellsPerChunkY);
    }

    public Cell getCellAtWorldPosition(Vector2 pos) {
        return getCellAtWorldPosition(pos.x, pos.y, false);
    }

    public Cell getCellAtWorldPosition(float x, float y, boolean bounded) {
        int cX = (int) (((x - boundary.x) / boundary.width) * chunksX * cellsPerChunkX);
        int cY = (int) (((y - boundary.y) / boundary.height) * chunksY * cellsPerChunkY);
        if (bounded) {
            cX = MathUtils.clamp(cX, 0, chunksX * cellsPerChunkX - 1);
            cY = MathUtils.clamp(cY, 0, chunksY * cellsPerChunkY - 1);
        }
        return getCellByCellId(cX, cY);
    }

    public Cell getCellByCellId(int x, int y) {
        //Work out chunks
        int chunkX = x / cellsPerChunkX;
        int chunkY = y / cellsPerChunkY;
        WorldChunk chunk = getChunkByChunkId(chunkX, chunkY);
        if (chunk != null) {
            return chunk.getCellByCellId(x, y);
        }
        return null;
    }

    public Cell getCellByOffset(Cell target, int x, int y) {
        return getCellByCellId(target.cellId.x + x, target.cellId.y + y);
    }

    public WorldChunk getChunkByChunkId(int x, int y) {
        if (x < 0 || y < 0 || x >= chunksX || y >= chunksY) {
            return null;
        }
        return chunks[x][y];
    }

    public WorldChunk getChunkByChunkId(GridPoint2 chunkId) {
        return getChunkByChunkId(chunkId.x, chunkId.y);
    }

    public void update(float delta) {
        // Update each cell
        boolean regionUpdatedRequired = false;
        for (int x = 0; x < chunksX; x++) {
            for (int y = 0; y < chunksY; y++) {
                WorldChunk chunk = chunks[x][y];
                if (chunk != null) {
                    chunk.update(delta);
                    regionUpdatedRequired = regionUpdatedRequired || chunk.updateRegionsRequired;
                }
            }
        }
        //Perform any layer updates
        if (regionUpdatedRequired) {
            for (int x = 0; x < chunksX; x++) {
                for (int y = 0; y < chunksY; y++) {
                    WorldChunk chunk = chunks[x][y];
                    if (chunk != null) {
                        chunk.updateRegions();

                        //Update Linked Nodes that regions need update
                    }
                }
            }
        }

    }

    public Entity getEntityByName(String entityName) {
        Entity entity = namedEntities.get(entityName);
        return entity;
    }

    public Entity getEntityByPos(Vector2 pos) {
        Entity bestEntity = null;
        float bestDistance = 0;
        for (Entity e : namedEntities.values()) {
            if (bestEntity == null) {
                bestEntity = e;
                bestDistance = bestEntity.pos.dst2(pos);
            } else {
                if (bestDistance > e.pos.dst2(pos)) {
                    bestDistance = e.pos.dst2(pos);
                    bestEntity = e;
                }
            }
        }
        return bestEntity;
    }

    public void fillAllCells(CellType type) {
        for (int x = 0; x < chunksX; x++) {
            for (int y = 0; y < chunksY; y++) {
                WorldChunk chunk = chunks[x][y];
                if (chunk != null) {
                    chunk.fillAllCells(type);
                }
            }
        }
    }

    public void loadAllChunks() {
        worldLoadingProcessor.loadAllChunks();
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void fillAllCells(Rectangle bounds, CellType type, boolean road) {
        GridPoint2 p1 = getCellAtWorldPosition(bounds.x, bounds.y, true).cellId;
        GridPoint2 p2 = getCellAtWorldPosition(bounds.x + bounds.width, bounds.y + bounds.height, true).cellId;

        for (int x = p1.x; x <= p2.x; x++) {
            for (int y = p1.y; y <= p2.y; y++) {
                getCellByCellId(x, y).setType(type, 0, road);
            }
        }
    }

    public void printRegions() {
        StringBuilder out = new StringBuilder();
        for (int y = 0; y < getCellsY(); y++) {
            for (int x = 0; x < getCellsX(); x++) {
                if (x != 0) {
                    out.append(",");
                }
                out.append("["+x+","+y+"] "+getCellByCellId(x, y).region.regionId);
            }
            out.append("\n");
        }
        System.out.println(out.toString());
    }

    public void printCellType() {
        StringBuilder out = new StringBuilder();
        for (int y = 0; y < getCellsY(); y++) {
            for (int x = 0; x < getCellsX(); x++) {
                if (x != 0) {
                    out.append(",");
                }
                out.append(StringUtils.leftPad(getCellByCellId(x, y).type.name(), 8, ' '));
            }
            out.append("\n");
        }
        System.out.println(out.toString());
    }

    public int getCellsX() {
        return chunksX * cellsPerChunkX;
    }

    public int getCellsY() {
        return chunksY * cellsPerChunkY;
    }

    public int getChunksX() {
        return chunksX;
    }

    public int getChunksY() {
        return chunksY;
    }
}
