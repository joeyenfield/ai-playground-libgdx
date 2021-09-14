package com.emptypocketstudios.boardgame.engine.entity.btree;

import com.emptypocketstudios.boardgame.engine.entity.Entity;

public class BTSequenceAny extends BTSequence {

    public BTSequenceAny(BTItem parent) {
        super(parent);
    }

    @Override
    public BTResult process() {
        for (int i = 0; i < items.size; i++) {
            BTResult result = items.get(i).process();
            switch (result) {
                case SUCCESS:
                case RUNNING:
                    return result;
            }
        }
        return BTResult.FAILURE;
    }

    @Override
    public String commandName() {
        return "ANY";
    }

    @Override
    public void setup(String command) {
    }

    @Override
    public void reset() {
        super.reset();
    }
}
