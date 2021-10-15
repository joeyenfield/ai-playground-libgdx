package com.emptypocketstudios.boardgame.engine.time;

public class Timer {

    long start;
    long time;

    public void tick() {
        start = System.currentTimeMillis();
    }

    public void tock() {
        time = System.currentTimeMillis() - start;
    }

    public void ptock(String message) {
        tock();
        System.out.println(message + time);
    }

}
