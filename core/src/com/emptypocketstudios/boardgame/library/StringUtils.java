package com.emptypocketstudios.boardgame.library;

public class StringUtils {

    public static String leftPad(String data, int width, char fill) {
        return new String(new char[width - data.length()]).replace('\0', fill) + data;
    }
}
