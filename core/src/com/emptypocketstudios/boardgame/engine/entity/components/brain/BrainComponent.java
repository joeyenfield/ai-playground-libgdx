package com.emptypocketstudios.boardgame.engine.entity.components.brain;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.EntityComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.manage.BTree;

public class BrainComponent extends EntityComponent implements Pool.Poolable {

    public Memory memory;
    public BTree behaviour;

    public BrainComponent(Entity entity) {
        super(entity);
        memory = new Memory(entity);
    }

    @Override
    public void update(float delta) {
        if (behaviour != null) {
            behaviour.tick();
        }
    }

    public void setBehaviour(BTree man) {
        this.behaviour = man;
        this.behaviour.setEntity(entity);
    }

    @Override
    public void reset() {
        Pools.free(behaviour);
        behaviour = null;
        memory.clear();
    }
}
