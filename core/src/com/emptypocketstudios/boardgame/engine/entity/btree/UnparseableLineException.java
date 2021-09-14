package com.emptypocketstudios.boardgame.engine.entity.btree;

public class UnparseableLineException extends RuntimeException {
    public UnparseableLineException(String s, Exception e) {
        super(s, e);
    }
}
