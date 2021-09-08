package com.emptypocketstudios.boardgame.engine.entity.components;

import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.NotificationTypes;

public class NotificationComponent extends EntityComponent {
    public short notification = NotificationTypes.THINKING;
    public float displayTime = -1;

    public NotificationComponent(Entity entity) {
        super(entity);
    }

    @Override
    public void update(float delta) {
        if (displayTime > 0) {
            displayTime -= delta;
            if (displayTime < 0) {
                notification = NotificationTypes.NONE;
            }
        }
    }
}
