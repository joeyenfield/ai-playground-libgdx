package com.emptypocketstudios.boardgame.engine.task;

import com.emptypocketstudios.boardgame.engine.messages.Message;
import com.emptypocketstudios.boardgame.engine.messages.MessageProcessor;

public class TaskManager implements MessageProcessor {

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }
}
