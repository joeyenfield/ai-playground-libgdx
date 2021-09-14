package com.emptypocketstudios.boardgame.engine.entity.btree.parser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.emptypocketstudios.boardgame.engine.entity.btree.BTCondition;
import com.emptypocketstudios.boardgame.engine.entity.btree.BTItem;
import com.emptypocketstudios.boardgame.engine.entity.btree.BTree;

public class BTreeParser {

    public int getIndentation(String data, char indentChar) {
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) != indentChar) {
                return i;
            }
        }
        return 0;
    }

    public BTree parseFile(String fileName) {
        FileHandle file = Gdx.files.absolute(fileName);
        return parseFileData(file.readString());
    }

    public static void main(String[] args) {
        BTreeParser parser = new BTreeParser();
        String data = "";
        data += "TREE rootTree\n";
        data += "\tANY hunger\n";
        data += "\t\tHUNGER \"WantFood\"\n";
        data += "\n";

    }

    public BTree parseFileData(String data) {
        String[] commands = data.split("\n");

        BTree parent = new BTree();
        int lastIndentation = 0;
        for (int i = 0; i < commands.length; i++) {
            String command = commands[i];
            int indent = getIndentation(command, '\t');
            BTItem item = getItem(parent, command);
            //Check next line is condition
            if (i + 1 < commands.length && isNextCondition(commands[i + 1])) {
                item.condition = getCondition(commands[i + 1]);
            }

            //No change in indentation
            if(indent == lastIndentation){

            }else if(indent > lastIndentation){

            }else if(indent < lastIndentation){

            }

            item.parent = parent;
        }
        return parent;
    }

    private boolean isNextCondition(String next) {
        return next.contains("CONDITION ");
    }

    private BTItem getItem(BTItem parent, String command) {
        return null;
    }

    private BTCondition getCondition(String command) {
        return null;
    }
}
