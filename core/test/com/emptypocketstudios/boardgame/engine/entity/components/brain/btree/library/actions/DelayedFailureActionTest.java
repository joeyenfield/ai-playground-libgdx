package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.BTFactory;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

import junit.framework.TestCase;

import org.junit.Test;

public class DelayedFailureActionTest extends TestCase {

    @Test
    public void testParsing() {
        FailureAction failureItem = BTFactory.getItem(new Command("FAILURE name:Joeys Testing, delay:1243", 1));
        assertNotNull(failureItem);
        assertEquals(1243, failureItem.delay);
        assertEquals("Joeys Testing", failureItem.name);
    }

}