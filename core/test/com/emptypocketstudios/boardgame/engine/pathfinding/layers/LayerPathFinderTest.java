package com.emptypocketstudios.boardgame.engine.pathfinding.layers;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellTypes;
import com.emptypocketstudios.boardgame.engine.world.RegionNode;
import com.emptypocketstudios.boardgame.engine.world.World;
import com.emptypocketstudios.boardgame.engine.world.WorldChunk;

import junit.framework.TestCase;

import org.junit.Test;

public class LayerPathFinderTest extends TestCase {

    World world;
    RegionNodePathFinder pathFinder;

    public void setUp() throws Exception {
        super.setUp();
        pathFinder = new RegionNodePathFinder();
        Rectangle region = new Rectangle(0, 0, 100, 100);
        world = new World(region, 10, 10, 10, 10);
        world.loadAllChunks();
        world.update(1);
    }

    @Test
    public void testSingleNode() {
        Array<RegionNode> path = new Array<>();
        Cell start = world.getCellByCellId(1, 1);
        Cell end = world.getCellByCellId(5, 5);
        pathFinder.search(world, start, end, path, false);
        assertEquals(1, path.size);
        assertEquals(0, path.get(0).chunkId.x);
        assertEquals(0, path.get(0).chunkId.y);
    }

    @Test
    public void testCrossNodes() {
        Array<RegionNode> path = new Array<>();
        Cell start = world.getCellByCellId(1, 1);
        Cell end = world.getCellByCellId(15, 1);

        //Verfity selected cells in the right chunks
        assertEquals(0, start.region.chunkId.x);
        assertEquals(0, start.region.chunkId.y);

        //Verfity selected cells in the right chunks
        assertEquals(1, end.region.chunkId.x);
        assertEquals(0, end.region.chunkId.y);


        pathFinder.search(world, start, end, path, false);

        RegionNode startRegion = new RegionNode();
        startRegion.set(0, 0, 1);
        RegionNode targetRegion = new RegionNode();
        targetRegion.set(1, 0, 1);

        assertEquals(2, path.size);
        assertTrue(path.contains(startRegion, false));
        assertTrue(path.contains(targetRegion, false));
    }

    @Test
    public void testCrossNodesHigher() {
        Array<RegionNode> path = new Array<>();
        Cell start = world.getCellByCellId(15, 95);
        Cell end = world.getCellByCellId(25, 95);

        //Verfity selected cells in the right chunks
        assertEquals(1, start.region.chunkId.x);
        assertEquals(9, start.region.chunkId.y);

        //Verfity selected cells in the right chunks
        assertEquals(2, end.region.chunkId.x);
        assertEquals(9, end.region.chunkId.y);


        pathFinder.search(world, start, end, path, false);

        RegionNode startRegion = new RegionNode();
        startRegion.set(1, 9, 1);
        RegionNode targetRegion = new RegionNode();
        targetRegion.set(2, 9, 1);

        assertEquals(2, path.size);
        assertTrue(path.contains(startRegion, false));
        assertTrue(path.contains(targetRegion, false));
    }


    @Test
    public void testCrossNodesWithWall() {
        Array<RegionNode> path = new Array<>();
        Cell start = world.getCellByCellId(1, 1);
        Cell end = world.getCellByCellId(15, 1);

        //Verfity selected cells in the right chunks
        assertEquals(0, start.region.chunkId.x);
        assertEquals(0, start.region.chunkId.y);

        //Verfity selected cells in the right chunks
        assertEquals(1, end.region.chunkId.x);
        assertEquals(0, end.region.chunkId.y);

        //Block off left side
        WorldChunk chunk = world.getChunkByChunkId(end.region.chunkId);
        for (int y = 0; y < chunk.numCellsY; y++) {
            chunk.cells[0][y].type = CellTypes.ROCK;
        }
        chunk.updateLayers();

        pathFinder.search(world, start, end, path, false);

        RegionNode startRegion = new RegionNode();
        startRegion.set(0, 0, 1);
        RegionNode targetRegion = new RegionNode();
        targetRegion.set(1, 0, 1);

        assertEquals(4, path.size);
        assertTrue(path.contains(startRegion, false));
        assertTrue(path.contains(targetRegion, false));
    }

    public void testAddRegionLinkDoesNothingIfCellsEmpty() {
        Array<RegionLinks> regions = new Array<>();
        pathFinder.addRegionLink(null, null, regions);
        assertEquals(0, regions.size);
    }

    public void testAddRegionLinkAddsNewLinkIfIdDoesNotAlreadyExist() {
        Array<RegionLinks> regions = new Array<>();
        Cell c1 = new Cell();
        Cell c2 = new Cell();
        c1.region.regionId = 1;
        c1.cellId.x = 0;
        c1.cellId.y = 0;
        c2.region.regionId = 1;
        c2.cellId.x = 1;
        c2.cellId.y = 0;
        pathFinder.addRegionLink(c1, c1, regions);
        assertEquals(1, regions.size);

        //If same cell tested dont add it
        Cell c3 = new Cell();
        Cell c4 = new Cell();
        c3.region.regionId = 1;
        c3.cellId.x = 0;
        c3.cellId.y = 0;
        c4.region.regionId = 1;
        c4.cellId.x = 1;
        c4.cellId.y = 0;

        pathFinder.addRegionLink(c3, c4, regions);
        assertEquals(1, regions.size);
    }

    @Test
    public void testGetLinksEdgeWhenSameRegion() {
        WorldChunk chunk = world.getChunkByChunkId(0, 0);
        assertNotNull(chunk);

        RegionLinks current = new RegionLinks();
        current.current = new RegionNode();
        current.current.set(0, 0, 1);

        Array<RegionLinks> links = new Array<>();
        pathFinder.getLinks(chunk, current, links);
        assertEquals(2, links.size);

        RegionLinks linkUp = new RegionLinks();
        linkUp.source = new RegionNode();
        linkUp.source.regionId = 1;
        linkUp.source.chunkId.set(0, 0);
        linkUp.current = new RegionNode();
        linkUp.current.regionId = 1;
        linkUp.current.chunkId.set(0, 1);

        RegionLinks linkRight = new RegionLinks();
        linkRight.source = new RegionNode();
        linkRight.source.regionId = 1;
        linkRight.source.chunkId.set(0, 0);
        linkRight.current = new RegionNode();
        linkRight.current.regionId = 1;
        linkRight.current.chunkId.set(1, 0);

        assertTrue(links.contains(linkUp, false));
        assertTrue(links.contains(linkRight, false));
    }

    @Test
    public void testGetLinksOnlyFollowsCurrentRegion() {
        WorldChunk chunk = world.getChunkByChunkId(0, 0);
        assertNotNull(chunk);
        chunk.cells[0][9].region.regionId = 10;
        RegionLinks current = new RegionLinks();
        current.current = new RegionNode();
        current.current.set(0, 0, 10);

        Array<RegionLinks> links = new Array<>();
        pathFinder.getLinks(chunk, current, links);
        assertEquals(1, links.size);
    }

    @Test
    public void testGetLinks() {
        Array<RegionNode> path = new Array<>();
        Cell start = world.getCellByCellId(15, 85);
        //Verfity selected cells in the right chunks
        assertEquals(1, start.region.chunkId.x);
        assertEquals(8, start.region.chunkId.y);

        RegionLinks regionLink = new RegionLinks();
        regionLink.current = new RegionNode();
        regionLink.current.set(start);

        Array<RegionLinks> links = new Array<>();
        pathFinder.getLinks(world.getChunkByChunkId(start.region.chunkId), regionLink, links);
        assertEquals(4, links.size);

        RegionLinks tester = new RegionLinks();
        tester.source = new RegionNode();
        tester.current = new RegionNode();

        tester.source.set(start);

        tester.current.set(0, 8, 1);
        assertTrue(links.contains(tester, false));

        tester.current.set(2, 8, 1);
        assertTrue(links.contains(tester, false));

        tester.current.set(1, 9, 1);
        assertTrue(links.contains(tester, false));

        tester.current.set(1, 7, 1);
        assertTrue(links.contains(tester, false));

    }

    @Test
    public void testGetLinksDoesNotFollowBlockedPaths() {
        /*
        Tese should be to make sure if next cell is blocked off it will not follow it
         */
    }
}