package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.manage;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.BTFactory;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.FailureAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions.SucceedAction;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

import junit.framework.TestCase;

import org.junit.Test;

public class BTSequenceAnyTest extends TestCase {

    public FailureAction failure(long delay) {
        return BTFactory.getItem(new Command("FAILURE delay:" + delay, 0));
    }

    public SucceedAction success(long delay) {
        return BTFactory.getItem(new Command("SUCCESS delay:" + delay, 0));
    }

    @Test
    public void testAllWithSuccess() throws InterruptedException {
        BTSequenceAny any = new BTSequenceAny();
        any.add(failure(500));
        any.add(failure(500));
        any.add(failure(500));
        any.add(failure(500));


        any.before();
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(1000);
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(1000);
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(1000);
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(1000);
        assertEquals(BTResult.FAILURE, any.tick());
        Thread.sleep(1000);
    }

    @Test
    public void testAllFailsWithAnyFailure() throws InterruptedException {
        BTSequenceAny any = new BTSequenceAny();
        any.add(failure(500));
        any.add(failure(500));
        any.add(failure(500));
        any.add(success(500));


        any.before();
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(1000);
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(1000);
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(1000);
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(1000);
        assertEquals(BTResult.SUCCESS, any.tick());
        Thread.sleep(1000);
    }

    @Test
    public void testDirectFailure() throws InterruptedException {
        BTSequenceAny any = new BTSequenceAny();
        any.add(failure(2000));

        any.before();
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(100);
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(100);
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(100);
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(2000);
        assertEquals(BTResult.FAILURE, any.tick());
    }

    @Test
    public void testDirectSuccess() throws InterruptedException {
        BTSequenceAny any = new BTSequenceAny();
        any.add(success(2000));

        any.before();
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(100);
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(100);
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(100);
        assertEquals(BTResult.RUNNING, any.tick());
        Thread.sleep(2000);
        assertEquals(BTResult.SUCCESS, any.tick());
    }
}