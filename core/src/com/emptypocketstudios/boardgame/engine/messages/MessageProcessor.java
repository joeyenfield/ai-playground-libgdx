package com.emptypocketstudios.boardgame.engine.messages;

public interface MessageProcessor<T extends Message> {
    public boolean handleMessage(T message);
}
