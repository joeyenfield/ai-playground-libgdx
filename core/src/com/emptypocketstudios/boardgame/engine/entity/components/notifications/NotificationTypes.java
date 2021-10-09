package com.emptypocketstudios.boardgame.engine.entity.components.notifications;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.HashMap;
import java.util.Locale;

public class NotificationTypes {
    public static final short NONE = 0;
    public static final short HUNGER = 10;
    public static final short HAPPY = 20;
    public static final short SAD = 30;
    public static final short SLEEP = 40;
    public static final short SLEEP_1 = 40;
    public static final short SLEEP_2 = 41;
    public static final short ANGRY = 50;
    public static final short THINKING = 60;
    public static final short THINKING_1 = 60;
    public static final short THINKING_2 = 61;
    public static final short THINKING_3 = 62;
    public static final short PATH_FOLLOW = 70;
    public static final short PATH_CONFUSED = 80;
    public static final short THIRST = 90;
    public static final short THIRST_1 = 90;
    public static final short THIRST_2 = 90;
    public static final short WOOD_CUTTING = 100;
    public static final short WOOD_PLANTING = 110;

    public static HashMap<Short, TextureAtlas.AtlasRegion> textures = new HashMap<>();

    public static short getId(String name) {
        switch (name.toUpperCase(Locale.ROOT).trim()) {
            case "HUNGER":
                return HUNGER;
            case "HAPPY":
                return HAPPY;
            case "SAD":
                return SAD;
            case "SLEEP":
                return SLEEP;
            case "ANGRY":
                return ANGRY;
            case "THINKING":
                return THINKING;
            case "PATH_FOLLOW":
                return PATH_FOLLOW;
            case "PATH_CONFUSED":
                return PATH_CONFUSED;
            case "THIRST":
                return THIRST;
            case "WOOD_CUTTING":
                return WOOD_CUTTING;
            case "WOOD_PLANTING":
                return WOOD_PLANTING;
        }
        return NONE;
    }

    public static String name(short type) {
        switch (type) {
            case HUNGER:
                return "HUNGER";
            case HAPPY:
                return "HAPPY";
            case SAD:
                return "SAD";
            case SLEEP:
                return "SLEEP";
            case ANGRY:
                return "ANGRY";
            case THINKING:
                return "THINKING";
            case PATH_FOLLOW:
                return "PATH_FOLLOW";
            case PATH_CONFUSED:
                return "PATH_CONFUSED";
            case THIRST:
                return "THIRST";
            case WOOD_CUTTING:
                return "WOOD_CUTTING";
            case WOOD_PLANTING:
                return "WOOD_PLANTING";
        }
        return "";
    }

    public static TextureAtlas.AtlasRegion getTexture(short notification) {
        if (notification == THINKING) {
            short idx = (short) ((System.currentTimeMillis() / 1000) % 3);
            notification += idx;
        }

        if (notification == SLEEP) {
            short idx = (short) ((System.currentTimeMillis() / 2000) % 2);
            notification += idx;
        }

        if (notification == THIRST) {
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
        textures.put(THIRST_1, atlas.findRegion("emotes/emote_drop"));
        textures.put((short) (THIRST_2+1), atlas.findRegion("emotes/emote_drops"));
        textures.put(WOOD_CUTTING, atlas.findRegion("emotes/emote_cutting_tree"));
        textures.put(WOOD_PLANTING, atlas.findRegion("emotes/emote_planting_tree"));

    }
}