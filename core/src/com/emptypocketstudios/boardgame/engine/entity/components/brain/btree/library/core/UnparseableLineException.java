package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core;

public class UnparseableLineException extends RuntimeException {
    public UnparseableLineException(String s, Exception e) {
        super(s, e);
    }
}
