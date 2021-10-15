package com.emptypocketstudios.boardgame.engine.world.processors;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;
import com.emptypocketstudios.boardgame.engine.time.Timer;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;

import java.util.HashMap;

public class WorldChunkRegionProcessor extends WorldChunkProcessor {
    public static final int RESET_LAYER_ID = -1000;

    public WorldChunkRegionProcessor(WorldChunk chunk) {
        super(chunk);
    }

    Pool<Array> regionArrayPool = new ReflectionPool<Array>(Array.class);


    protected void removeRegionLink(Cell sourceCell, Cell targetCell, HashMap<RegionNode, Array<RegionNode>> linkMap) {
        if (sourceCell != null && targetCell != null) {
            RegionNode sourceRegion = sourceCell.region;
            RegionNode targetRegion = targetCell.region;

            //Remove Source Mapping
            Array<RegionNode> sourcelinks = linkMap.get(sourceRegion);
            if (sourcelinks != null) {
                sourcelinks.clear();
                regionArrayPool.free(sourcelinks);
            }
            linkMap.remove(sourceRegion);

            //Setup Target links
            Array<RegionNode> targetLinks = linkMap.get(targetRegion);
            if (targetLinks != null) {
                targetLinks.removeValue(sourceRegion, false);
            }
        }
    }

    protected void addRegionLink(Cell sourceCell, Cell targetCell, HashMap<RegionNode, Array<RegionNode>> linkMap) {
        if (sourceCell != null && targetCell != null) {
            RegionNode sourceRegion = sourceCell.region;
            RegionNode targetRegion = targetCell.region;

            //Setup Source Mapping
            Array<RegionNode> sourcelinks = linkMap.get(sourceRegion);
            if (sourcelinks == null) {
                sourcelinks = regionArrayPool.obtain();
                linkMap.put(sourceRegion, sourcelinks);
            }
            if (!sourcelinks.contains(targetRegion, false)) {
                sourcelinks.add(targetRegion);
            }
            //Setup Target links
            if (!sourceRegion.equals(targetRegion)) {
                Array<RegionNode> targetLinks = linkMap.get(targetRegion);
                if (targetLinks == null) {
                    targetLinks = regionArrayPool.obtain();
                    linkMap.put(targetRegion, targetLinks);
                }
                if (!targetLinks.contains(sourceRegion, false)) {
                    targetLinks.add(sourceRegion);
                }
            }
        }
    }

    public void processLinks(boolean remove) {
        Timer timer = new Timer();
        Cell start;
        int dx = 0;
        int dy = 0;
        for (int x = 0; x < chunk.getCellsX(); x++) {
            for (int y = 0; y < chunk.getCellsY(); y++) {
                //Up
                start = chunk.getCellByIndex(x, y);
                dx = 0;
                dy = -1;
                if (remove) {
                    removeRegionLink(start, start.getLink(dx, dy), chunk.world.regionLinks);
                } else {
                    addRegionLink(start, start.getLink(dx, dy), chunk.world.regionLinks);
                }
                //Down
                dx = 0;
                dy = 1;
                if (remove) {
                    removeRegionLink(start, start.getLink(dx, dy), chunk.world.regionLinks);
                } else {
                    addRegionLink(start, start.getLink(dx, dy), chunk.world.regionLinks);
                }

                //Left
                dx = -1;
                dy = 0;
                if (remove) {
                    removeRegionLink(start, start.getLink(dx, dy), chunk.world.regionLinks);
                } else {
                    addRegionLink(start, start.getLink(dx, dy), chunk.world.regionLinks);
                }
                //Right
                dx = 1;
                dy = 0;
                if (remove) {
                    removeRegionLink(start, start.getLink(dx, dy), chunk.world.regionLinks);
                } else {
                    addRegionLink(start, start.getLink(dx, dy), chunk.world.regionLinks);
                }
                //Down Left
                dx = -1;
                dy = -1;
                if (remove) {
                    removeRegionLink(start, start.getLink(dx, dy), chunk.world.diagonalRegionLinks);
                } else {
                    addRegionLink(start, start.getLink(dx, dy), chunk.world.diagonalRegionLinks);
                }

                //Up Right
                dx = 1;
                dy = 1;
                if (remove) {
                    removeRegionLink(start, start.getLink(dx, dy), chunk.world.diagonalRegionLinks);
                } else {
                    addRegionLink(start, start.getLink(dx, dy), chunk.world.diagonalRegionLinks);
                }

                //Up Left
                dx = -1;
                dy = 1;
                if (remove) {
                    removeRegionLink(start, start.getLink(dx, dy), chunk.world.diagonalRegionLinks);
                } else {
                    addRegionLink(start, start.getLink(dx, dy), chunk.world.diagonalRegionLinks);
                }

                //Down Right
                dx = 1;
                dy = -1;
                if (remove) {
                    removeRegionLink(start, start.getLink(dx, dy), chunk.world.diagonalRegionLinks);
                } else {
                    addRegionLink(start, start.getLink(dx, dy), chunk.world.diagonalRegionLinks);
                }
            }
        }
    }

    @Override
    protected void run() {
        //Remove Links
        processLinks(true);

        //Reset Layer Id's
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                chunk.cells[x][y].region.regionId = RESET_LAYER_ID;
            }
        }
        //Update Regions
        int layerId = 1;
        for (int x = 0; x < chunk.numCellsX; x++) {
            for (int y = 0; y < chunk.numCellsY; y++) {
                if (expand(x, y, null, layerId) > 0) {
                    layerId++;
                }
            }
        }

        //Add Links
        processLinks(false);
    }

    private boolean isSameRegion(Cell currentCell, Cell parent) {
        if (parent == null) {
            return true;
        }
        return parent.type == currentCell.type && parent.isRoad == currentCell.isRoad && parent.typeVariant == currentCell.typeVariant;
    }

    private int expand(int x, int y, Cell parent, int newLayerId) {
        int updatedCount = 0;
        Cell c = chunk.getCellByIndex(x, y);
        if (c != null && c.region.regionId == RESET_LAYER_ID) {
            if (isSameRegion(c, parent)) {
                c.region.regionId = newLayerId;
                c.region.regionWalkWeight = CellType.getTravelEffort(c);
                updatedCount++;
                updatedCount += expand(x - 1, y, c, newLayerId);
                updatedCount += expand(x + 1, y, c, newLayerId);
                updatedCount += expand(x, y - 1, c, newLayerId);
                updatedCount += expand(x, y + 1, c, newLayerId);

//            if (chunk.world.allowDiagnoals) {
//                updatedCount += expand(x - 1, y - 1, newLayerId);
//                updatedCount += expand(x + 1, y + 1, newLayerId);
//                updatedCount += expand(x - 1, y + 1, newLayerId);
//                updatedCount += expand(x + 1, y - 1, newLayerId);
//            }
            }
        }
        return updatedCount;
    }
}
