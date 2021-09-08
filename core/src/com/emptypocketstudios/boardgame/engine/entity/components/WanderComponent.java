package com.emptypocketstudios.boardgame.engine.entity.components;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.library.RectangeUtils;

public class WanderComponent extends EntityComponent {
    public float stableTime = 10;

    public WanderComponent(Entity entity) {
        super(entity);
    }

    @Override
    public void update(float delta) {
        stableTime -= delta;
        if (stableTime < 0) {
            PathFollowComponent pathFollow = entity.getEntityComponent(PathFollowComponent.class);
            if (!pathFollow.isFollowingPath() && !pathFollow.thinkingAboutPath) {
                Vector2 pos = new Vector2();
//                pos.set(entity.world.boundary.width/2, entity.world.boundary.height/2);
//                pos.rotateDeg(MathUtils.random(360));
//                pos.scl(MathUtils.random(-0.1f,0.1f));
//                pos.add(entity.pos);
                float distance = entity.world.boundary.width/Math.min(entity.world.engine.chunksX, 6f);
                do {
                    RectangeUtils.randomPoint(entity.world.boundary, pos);
                }
                while (pos.dst2(entity.pos) > distance * distance);
                pathFollow.travelTo(entity.world.getCellAtWorldPosition(pos), false, false, false);
            }
            stableTime = 10 + MathUtils.random(30);
        }
    }
}
