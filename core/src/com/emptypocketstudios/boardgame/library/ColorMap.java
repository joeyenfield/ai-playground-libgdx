package com.emptypocketstudios.boardgame.library;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;

import java.util.HashMap;
import java.util.Random;

public class ColorMap {
    static HashMap<Byte, Color> colors = new HashMap<>();
    public static Color getRandomColor(byte value){
        if(!colors.containsKey(value)){
            Random random = new RandomXS128();
            random.setSeed(value);
            colors.put(value,new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1));
        }
        return colors.get(value);
    }
}
