package com.emptypocketstudios.boardgame.engine.entity.fsm;

public interface State {
    public void update(float delta);
    public void enterState();
    public void leavingState();
}
