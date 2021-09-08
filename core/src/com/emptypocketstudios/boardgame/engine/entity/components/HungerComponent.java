package com.emptypocketstudios.boardgame.engine.entity.components;

import com.emptypocketstudios.boardgame.engine.entity.Entity;

public class HungerComponent extends EntityComponent {
    float food = 100;

    float eatRate = 10;
    public HungerComponent(Entity entity) {
        super(entity);
    }

    @Override
    public void update(float delta) {
        food-=eatRate*delta;
    }
}
