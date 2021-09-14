package com.emptypocketstudios.boardgame.engine.entity.btree;

import com.emptypocketstudios.boardgame.engine.entity.Entity;

public class BTSequenceAll extends BTSequence {

    public BTSequenceAll(BTItem parent) {
        super(parent);
    }

    @Override
    public BTResult process() {
        for (int i = 0; i < items.size; i++) {
            BTResult result = items.get(i).process();
            switch (result) {
                case FAILURE:
                case RUNNING:
                    return result;
            }
        }
        return BTResult.SUCCESS;
    }

    @Override
    public String commandName() {
        return null;
    }

    @Override
    public void setup(String command) {

    }

    @Override
    public void reset() {
        super.reset();
    }
}
