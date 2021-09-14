package com.emptypocketstudios.boardgame.engine.entity.btree;

import com.emptypocketstudios.boardgame.engine.entity.Entity;

import java.util.HashMap;

public class BTree extends BTItem {
    String name;
    BTItem child;

    public BTree(Entity target) {
        this();
        memory.put("ENTITY", target);
    }

    public BTree() {
        super(new HashMap<String, Object>());
    }

    public BTree(BTItem parent) {
        super(parent);
    }

    @Override
    public BTResult process() {
        return child.process();
    }

    @Override
    public String commandName() {
        return "ROOT";
    }

    @Override
    public void setup(String command) {

    }
}
