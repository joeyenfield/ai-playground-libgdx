package com.emptypocketstudios.boardgame.engine.entity.btree.library.conditions;

import com.emptypocketstudios.boardgame.engine.entity.btree.UnparseableLineException;

import junit.framework.TestCase;

import org.junit.Test;

public class HungerConditionTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testParse(){
        String message="HUNGER \"1.4\"";
        HungerCondition hunger = new HungerCondition();
        hunger.parse(message);
        assertEquals(1.4, hunger.hungerThreshold, 0.05);
    }

    @Test
    public void testParseFailsInvalidValueBetweenQuotes() {
        String message = "HUNGER \"abcd\"";
        HungerCondition hunger = new HungerCondition();
        try {
            hunger.parse(message);
            fail();
        }catch(UnparseableLineException e){
        }
    }

    @Test
    public void testParseFailsInvalidFormatString() {
        String message = "SOMETHING \"abcd\"";
        HungerCondition hunger = new HungerCondition();
        try {
            hunger.parse(message);
            fail();
        }catch(UnparseableLineException e){
        }
    }

    @Test
    public void testParseDoesNotFailsTooManyParameters() {
        String message = "HUNGER \"1.5\" \"2\"";
        HungerCondition hunger = new HungerCondition();
        try {
            hunger.parse(message);
            fail();
        }catch(UnparseableLineException e){
        }
    }

    @Test
    public void testParseFailsTooManyParameters() {
        String message = "HUNGER \"1.5\" \"ABCD\"";
        HungerCondition hunger = new HungerCondition();
        try {
            hunger.parse(message);
            fail();
        }catch(UnparseableLineException e){
        }
    }
}