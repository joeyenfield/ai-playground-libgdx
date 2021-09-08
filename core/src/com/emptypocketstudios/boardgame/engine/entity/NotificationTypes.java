package com.emptypocketstudios.boardgame.engine.entity;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.emptypocketstudios.boardgame.engine.entity.components.PathFollowComponent;

import java.util.HashMap;

public class NotificationTypes {
    public static final Short NONE = 0;
    public static final Short HUNGER = 10;
    public static final Short HAPPY = 20;
    public static final Short SAD = 30;
    public static final Short SLEEP = 40;
    public static final Short SLEEP_1 = 40;
    public static final Short SLEEP_2 = 41;
    public static final Short ANGRY = 50;
    public static final Short THINKING = 60;
    public static final Short THINKING_1 = 60;
    public static final Short THINKING_2 = 61;
    public static final Short THINKING_3 = 62;
    public static final Short PATH_FOLLOW = 70;
    public static final Short PATH_CONFUSED = 80;

    public static HashMap<Short, TextureAtlas.AtlasRegion> textures = new HashMap<>();

    public static TextureAtlas.AtlasRegion getTexture(short notification) {
        if (notification == THINKING) {
            short idx = (short) ((System.currentTimeMillis() / 1000) % 3);
            notification += idx;
        }

        if (notification == SLEEP) {
            short idx = (short) ((System.currentTimeMillis() / 2000) % 2);
            notification += idx;
        }

        if (textures.containsKey(notification)) {
            return textures.get(notification);
        } else {
            System.out.println("Failed : " + notification);
            return textures.get(NONE);
        }
    }

    public static void setupAtlas(TextureAtlas atlas) {
        textures.put(NONE, atlas.findRegion("emotes/emote_idea"));
        textures.put(HUNGER, atlas.findRegion("emotes/emote_hunger"));
        textures.put(HAPPY, atlas.findRegion("emotes/emote_faceHappy"));
        textures.put(SAD, atlas.findRegion("emotes/emote_faceSad"));
        textures.put(ANGRY, atlas.findRegion("emotes/emote_faceAngry"));
        textures.put(SLEEP_1, atlas.findRegion("emotes/emote_sleep"));
        textures.put(SLEEP_2, atlas.findRegion("emotes/emote_sleeps"));
        textures.put(PATH_FOLLOW, atlas.findRegion("emotes/emote_pathfollow"));
        textures.put(THINKING_1, atlas.findRegion("emotes/emote_dots1"));
        textures.put(THINKING_2, atlas.findRegion("emotes/emote_dots2"));
        textures.put(THINKING_3, atlas.findRegion("emotes/emote_dots3"));
        textures.put(PATH_CONFUSED, atlas.findRegion("emotes/emote_swirl"));

    }
}