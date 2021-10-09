package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser;

public class ParseException extends RuntimeException {
    String command;
    long line;

    public ParseException(String message, Throwable t) {
        super(message, t);
    }

    public ParseException(String message, Command command) {
        this(message, command, null);
    }

    public ParseException(String message, String command, long line) {
        this(message, command, line, null, null);
    }

    public ParseException(String message, Command command, Throwable t) {
        this(message, command.getCommand(), command.getLine(), command.getSource(), t);
    }

    public ParseException(String message, String command, long line, String source, Throwable t) {
        super("Error:[" + message + "], Line:[" + line + "], command:[" + command + "], source:[" + source + "]", t);
        this.command = command;
        this.line = line;
    }
}
