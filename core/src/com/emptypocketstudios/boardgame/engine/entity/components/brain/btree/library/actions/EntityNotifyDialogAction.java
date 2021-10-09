package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.actions;

import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.BrainComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTLeafNode;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core.BTResult;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;
import com.emptypocketstudios.boardgame.engine.entity.components.notifications.NotificationComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.notifications.NotificationTypes;

public class EntityNotifyDialogAction extends BTLeafNode {
    public static final String COMMAND_NAME = "NOTIFY_DIALOG";
    long displayTime = 0;
    short type;
    boolean wait = false;
    long delayUntill;
    boolean freshRun = false;

    String memoryEntityName;

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void before() {
        delayUntill = System.currentTimeMillis() + displayTime;
        freshRun = true;
    }

    @Override
    public void after() {
    }

    @Override
    public BTResult process() {
        Entity targetEntity = null;
        if (memoryEntityName == null) {
            targetEntity = this.entity;
        } else {
            BrainComponent brain = entity.getEntityComponent(BrainComponent.class);
            String searchName = brain.memory.getString(memoryEntityName);
            targetEntity = this.entity.world.getEntityByName(searchName);
        }

        if (freshRun) {
            NotificationComponent notificationComponent = targetEntity.getEntityComponent(NotificationComponent.class);
            notificationComponent.notification = type;
            notificationComponent.displayTime = displayTime / 1000f;
        }

        if (wait && System.currentTimeMillis() < delayUntill) {
            return BTResult.RUNNING;
        }
        return BTResult.SUCCESS;
    }

    @Override
    public void parse(Command command) {
        type = NotificationTypes.getId(command.getArg("type"));
        displayTime = command.getArgLong("displayTime", -1);
        wait = command.getArgBoolean("wait", false);
        memoryEntityName = command.getArg("memoryEntityName", null);
    }
}
