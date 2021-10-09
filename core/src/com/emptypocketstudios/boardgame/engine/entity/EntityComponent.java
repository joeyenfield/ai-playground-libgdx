package com.emptypocketstudios.boardgame.engine.entity;

public abstract class EntityComponent {
    protected Entity entity;

    public EntityComponent(Entity entity) {
        this.entity = entity;
    }

    public EntityComponent setEntity(Entity entity) {
        this.entity = entity;
        return this;
    }

    public abstract void update(float delta);

}
