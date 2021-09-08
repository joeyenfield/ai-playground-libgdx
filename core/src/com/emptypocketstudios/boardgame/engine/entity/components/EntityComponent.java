package com.emptypocketstudios.boardgame.engine.entity.components;

import com.emptypocketstudios.boardgame.engine.entity.Entity;

public abstract class EntityComponent {

    Entity entity;

    public EntityComponent(Entity entity) {
        this.entity = entity;
    }

    public abstract void update(float delta);
}
