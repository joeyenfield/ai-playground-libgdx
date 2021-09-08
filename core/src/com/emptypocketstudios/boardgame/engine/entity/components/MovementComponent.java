package com.emptypocketstudios.boardgame.engine.entity.components;

import com.badlogic.gdx.math.Vector2;
import com.emptypocketstudios.boardgame.engine.entity.Entity;

import java.awt.Component;

public class MovementComponent extends EntityComponent {
    public boolean limitToWorld = true;
    public Vector2 vel = new Vector2();
    public float maxSpeed = 10;

    public MovementComponent(Entity entity) {
        super(entity);
    }

    @Override
    public void update(float delta) {
        entity.pos.x += vel.x * delta;
        entity.pos.y += vel.y * delta;

        if (limitToWorld) {
            if (entity.pos.x > entity.world.boundary.x + entity.world.boundary.width) {
                entity.pos.x = entity.world.boundary.x + entity.world.boundary.width;
                vel.x *= -1;
            }
            if (entity.pos.x < entity.world.boundary.x) {
                entity.pos.x = entity.world.boundary.x;
                vel.x *= -1;
            }

            if (entity.pos.y > entity.world.boundary.y + entity.world.boundary.height) {
                entity.pos.y = entity.world.boundary.y + entity.world.boundary.height;
                vel.y *= -1;
            }

            if (entity.pos.y < entity.world.boundary.y) {
                entity.pos.y = entity.world.boundary.y;
                vel.y *= -1;
            }
        }
    }
}
