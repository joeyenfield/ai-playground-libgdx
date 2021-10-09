package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser;

import junit.framework.TestCase;

public class CommandParserTest extends TestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testIsValidCommandline(){
        assertTrue(CommandParser.isValidCommandLine("ABCDE asdafds"));
        assertFalse(CommandParser.isValidCommandLine("     "));
        assertFalse(CommandParser.isValidCommandLine("     //"));
        assertFalse(CommandParser.isValidCommandLine("//"));
    }

    public void testParseFileDataEmptyTree() {
        String data = "";
        data += "BTREE name:rootTree\n";
        Command tree = CommandParser.getCommand(data);

        assertNotNull(tree);
        assertEquals("BTREE", tree.getCommand());
        assertEquals("rootTree", tree.getName());
        assertEquals(0, tree.subCommands.size);
        assertEquals(0, tree.conditions.size);
        assertEquals(0, tree.getIndent());
    }

    public void testParseFileDataRemoveCaseOnFields() {
        String data = "";
        data += "BTREE NaMe:rootTree\n";
        Command tree = CommandParser.getCommand(data);

        assertNotNull(tree);
        assertEquals("BTREE", tree.getCommand());
        assertEquals("rootTree", tree.getName());
    }

    public void testParseFileDataParseArgs() {
        String data = "";
        data += "BTREE name:rootTree, key:value, key2 : value2, key3:\"value,data\"\n";
        Command tree = CommandParser.getCommand(data);
        assertNotNull(tree);
        assertEquals("BTREE", tree.getCommand());
        assertEquals("rootTree", tree.getName());
        assertEquals(0, tree.subCommands.size);
        assertEquals(0, tree.conditions.size);
        assertEquals(0, tree.getIndent());
        assertEquals("value", tree.getArg("key"));
        assertEquals("value2", tree.getArg("key2"));
        assertEquals("value,data", tree.getArg("key3"));

    }

    public void testGetCommandFromText() {
        String data = "";
        data += "BTREE name:rootTree\n";
        data += "\tANY name:BRAIN\n";
        data += "\t\tANY name:HUNGRY\n";
        data += "\t\tCONDITION HUNGER name: HUNGER, threshold:1\n";
        data += "\t\t\tALL name:Track Down Food\n";
        data += "\t\t\t\tFIND name:Look for Food, range:5, type:FOOD, targetName:FOOD\n";
        data += "\t\t\t\tTARGET name:Find Path To Food, target:FOOD\n";
        data += "\t\t\t\tFOLLOW name:Follow Path To Food,\n";
        data += "\t\tALL name:Wander Food\n";
        data += "\t\t\tWANDER name:Nothing to do, time:5s";
        Command tree = CommandParser.getCommand(data);
        tree.print(0, false);
        assertNotNull(tree);
        assertEquals("BTREE", tree.getCommand());
        assertEquals("rootTree", tree.getName());
        assertEquals(1, tree.subs());
        assertEquals(0, tree.conditions.size);

        assertEquals("ANY", tree.sub(0).getCommand());
        assertEquals("BRAIN", tree.sub(0).getName());
        assertEquals(2, tree.sub(0).subs());

        assertEquals("ANY", tree.sub(0).sub(0).getCommand());
        assertEquals("HUNGRY", tree.sub(0).sub(0).getName());
        assertEquals(1, tree.sub(0).sub(0).subs());

        assertEquals("ALL", tree.sub(0).sub(1).getCommand());
        assertEquals("Wander Food", tree.sub(0).sub(1).getName());
        assertEquals(1, tree.sub(0).sub(1).subs());
    }

    public void testBigStepBack() {
        String data = "";
        data += "BTREE name:rootTree\n";
        data += "\tANY name:BRAIN\n";
        data += "\t\tANY name:HUNGRY\n";
        data += "\t\tCONDITION HUNGER name: HUNGER, threshold:1\n";
        data += "\t\t\tALL name:Track Down Food\n";
        data += "\t\t\t\tFIND name:Look for Food, range:5, type:FOOD, targetName:FOOD\n";
        data += "\t\t\t\tTARGET name:Find Path To Food, target:FOOD\n";
        data += "\t\t\t\tFOLLOW name:Follow Path To Food,\n";
        data += "\tALL name:Wander Food\n";
        data += "\t\tWANDER name:Nothing to do, time:5s";
        Command tree = CommandParser.getCommand(data);
        tree.print(0, false);
        assertNotNull(tree);
        assertEquals("BTREE", tree.getCommand());
        assertEquals("rootTree", tree.getName());
        assertEquals(2, tree.subs());
        assertEquals(0, tree.conditions.size);

        assertEquals("ANY", tree.sub(0).getCommand());
        assertEquals("BRAIN", tree.sub(0).getName());
        assertEquals(1, tree.sub(0).subs());

        assertEquals("ANY", tree.sub(0).sub(0).getCommand());
        assertEquals("HUNGRY", tree.sub(0).sub(0).getName());
        assertEquals(1, tree.sub(0).sub(0).subs());

        assertEquals("ALL", tree.sub(1).getCommand());
        assertEquals("Wander Food", tree.sub(1).getName());
        assertEquals(1, tree.sub(1).subs());
    }


    public void testMultiDepth() {
        String data = "";
        data += "BTREE                    name:l1                                                \n";
        data += "\tANY                    name:l1.Any 1                                          \n";
        data += "\t\tANY                  name:l1.Any 1.Any 1                                    \n";
        data += "\t\tCONDITION HUNGER     name: HUNGER, threshold:1                              \n";
        data += "\t\t\tALL                name:l1.Any 1.Any 1.All 1                              \n";
        data += "\t\t\t\tSUCCESS          name:l1.Any 1.Any 1.All 1.Success1, delay:0            \n";
        data += "\t\t\t\tSUCCESS          name:l1.Any 1.Any 1.All 1.Success2, delay:0            \n";
        data += "\t\t\tANY                name:l1.Any 1.Any 1.All 2                              \n";
        data += "\t\t\t\tSUCCESS          name:l1.Any 1.Any 1.All 2.Success1, delay:0            \n";
        data += "\t\t\t\tSUCCESS          name:l1.Any 1.Any 1.All 2.Success2, delay:0            \n";
        data += "\t\tALL                  name:l1.Any 1.All 2                                          \n";
        data += "\t\t\tWANDER             name:l1.Any 1.All 2.Wander, time:5s";

        Command tree = CommandParser.getCommand(data);
        tree.print(0, false);
        assertNotNull(tree);
        assertEquals("BTREE", tree.getCommand());
        assertEquals("l1", tree.getName());
        assertEquals(1, tree.subs());
        assertEquals(0, tree.conditions.size);

        assertEquals("ANY", tree.sub(0).getCommand());
        assertEquals("l1.Any 1", tree.sub(0).getName());
        assertEquals(2, tree.sub(0).subs());
        assertEquals(0, tree.sub(0).conditions.size);

        assertEquals("ANY", tree.sub(0).sub(0).getCommand());
        assertEquals("l1.Any 1.Any 1", tree.sub(0).sub(0).getName());
        assertEquals(2, tree.sub(0).sub(0).subs());
        assertEquals(1, tree.sub(0).sub(0).conditions.size);

        assertEquals("ALL", tree.sub(0).sub(1).getCommand());
        assertEquals("l1.Any 1.All 2", tree.sub(0).sub(1).getName());
        assertEquals(1, tree.sub(0).sub(1).subs());
        assertEquals(0, tree.sub(0).sub(1).conditions.size);


    }
}