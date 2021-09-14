package com.emptypocketstudios.boardgame.engine.entity.components;

import com.emptypocketstudios.boardgame.engine.entity.Entity;

public class HungerComponent extends EntityComponent {
    public float hunger = 0;

    float eatRate = 10;
    public HungerComponent(Entity entity) {
        super(entity);
    }

    @Override
    public void update(float delta) {
        hunger+=eatRate*delta;
    }
}
