package com.emptypocketstudios.boardgame.engine.messages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Queue;

import java.util.HashMap;
import java.util.Map;


public class PostOffice<T extends Message> implements MessageProcessor<T> {
    Map<String, MessageProcessor> messageHandlers = new HashMap<>();
    Queue<T> messageQueue = new Queue<>();

    public void register(String name, MessageProcessor processor) {
        this.messageHandlers.put(name, processor);
    }

    public void unregister(String name) {
        this.messageHandlers.remove(name);
    }

    @Override
    public boolean handleMessage(T message) {
        return false;
    }

    public void process(long maxTime) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < maxTime && messageQueue.size > 0) {
//            Gdx.app.log("POST-OFFICE", "Sending Message");
            T message = messageQueue.removeFirst();
            if (!routeMessage(message)) {
                Gdx.app.log("POST-OFFICE", "Routing Failed");
                Pools.free(message);
            }
        }
    }

    private boolean routeMessage(T message) {
//        Gdx.app.log("POST-OFFICE", "Route Message ["+message.target+"]");
        MessageProcessor processor = messageHandlers.get(message.target);
        if (processor != null) {
            return processor.handleMessage(message);
        } else {
//            Gdx.app.error("POST-OFFICE", "No Handler found for ["+message.target+"]");
        }
        return false;
    }

    public void send(T message) {
        this.messageQueue.addLast(message);
    }

    public int getMessageCount() {
        return messageQueue.size;
    }
}
