package com.emptypocketstudios.boardgame.engine;

import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.emptypocketstudios.boardgame.engine.world.Cell;

import java.util.Random;

public class RandomUtil {

    Random random;

    public RandomUtil(long seed) {
        random = new RandomXS128(seed);
    }

    public void setSeed(long seed){
        random.setSeed(seed);
    }

    public int random(int range){
        return random.nextInt(range);
    }

    public float random(float range) {
        return random.nextFloat() * range;
    }

    public float random(float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    public int random(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    public int randomSign() {
        return 1 | (random.nextInt() >> 31);
    }

    public boolean randomBoolean() {
        return random.nextBoolean();
    }

    public void randomPoint(Rectangle rec, Vector2 pos) {
        pos.x = rec.x + random(rec.width);
        pos.y = rec.y + random(rec.height);
    }

    public <T> T random(Array<T> cells) {
        return cells.get(random(0, cells.size-1));
    }
}
