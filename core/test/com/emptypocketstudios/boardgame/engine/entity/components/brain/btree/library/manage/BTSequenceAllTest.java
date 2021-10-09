package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.manage;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.BTFactory;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.FailureAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.SucceedAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

import junit.framework.TestCase;

import org.junit.Test;

public class BTSequenceAllTest extends TestCase {

    public FailureAction failure(long delay) {
        return BTFactory.getItem(new Command("FAILURE delay:" + delay, 0));
    }

    public SucceedAction success(long delay) {
        return BTFactory.getItem(new Command("SUCCESS delay:" + delay, 0));
    }

    @Test
    public void testAllWithSuccess() throws InterruptedException {
        BTSequenceAll all = new BTSequenceAll();
        all.add(success(500));
        all.add(success(500));
        all.add(success(500));
        all.add(success(500));


        all.before();
        assertEquals(BTResult.RUNNING, all.tick());
        Thread.sleep(1000);
        assertEquals(BTResult.RUNNING, all.tick());
        Thread.sleep(1000);
        assertEquals(BTResult.RUNNING, all.tick());
        Thread.sleep(1000);
        assertEquals(BTResult.RUNNING, all.tick());
        Thread.sleep(1000);
        assertEquals(BTResult.SUCCESS, all.tick());
        Thread.sleep(1000);
    }

    @Test
    public void testAllFailsWithAnyFailure() throws InterruptedException {
        BTSequenceAll all = new BTSequenceAll();
        all.add(success(500));
        all.add(success(500));
        all.add(failure(500));
        all.add(success(500));


        all.before();
        assertEquals(BTResult.RUNNING, all.tick());
        Thread.sleep(1000);
        assertEquals(BTResult.RUNNING, all.tick());
        Thread.sleep(1000);
        assertEquals(BTResult.RUNNING, all.tick());
        Thread.sleep(1000);
        assertEquals(BTResult.FAILURE, all.tick());
        Thread.sleep(1000);
    }

    @Test
    public void testDirectFailure() throws InterruptedException {
        BTSequenceAll all = new BTSequenceAll();
        all.add(failure(2000));

        all.before();
        assertEquals(BTResult.RUNNING, all.tick());
        Thread.sleep(100);
        assertEquals(BTResult.RUNNING, all.tick());
        Thread.sleep(100);
        assertEquals(BTResult.RUNNING, all.tick());
        Thread.sleep(100);
        assertEquals(BTResult.RUNNING, all.tick());
        Thread.sleep(2000);
        assertEquals(BTResult.FAILURE, all.tick());
    }
}