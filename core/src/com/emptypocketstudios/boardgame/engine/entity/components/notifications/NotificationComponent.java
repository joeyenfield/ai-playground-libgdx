package com.emptypocketstudios.boardgame.engine.entity.components.notifications;

import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.EntityComponent;

public class NotificationComponent extends EntityComponent {
    public short notification = NotificationTypes.NONE;
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
