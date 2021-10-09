package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.conditions;

import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.UnparseableLineException;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;

import junit.framework.TestCase;

import org.junit.Test;

public class HungerConditionTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testParse(){
        String text="HUNGER threshold:1.4";
        Command message = new Command();
        message.setText(text,1);

        HungerCondition hunger = new HungerCondition();
        hunger.parse(message);
        assertEquals(1.4, hunger.threshold, 0.05);
    }

}