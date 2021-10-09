package com.emptypocketstudios.boardgame.engine.entity;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.emptypocketstudios.boardgame.engine.entity.components.building.BuildingComponent;
import com.emptypocketstudios.boardgame.engine.entity.components.building.BuildingType;
import com.emptypocketstudios.boardgame.engine.entity.components.human.HumanType;
import com.emptypocketstudios.boardgame.engine.entity.components.human.LifeComponent;

import java.util.HashMap;

public class EntityTextureAtlas {
    public static HashMap<HumanType, TextureAtlas.AtlasRegion> humanTextures = new HashMap<>();
    public static HashMap<BuildingType, TextureAtlas.AtlasRegion> buildingTextures = new HashMap<>();

    public static TextureAtlas.AtlasRegion[] getBuildingTexture(Entity e) {
        if (e.type == EntityType.BUILDING) {
            BuildingComponent build = e.getEntityComponent(BuildingComponent.class);
            if (buildingTextures.containsKey(build.type)) {
                if (build.type == BuildingType.CASTLE) {
                    return new TextureAtlas.AtlasRegion[]{
                            buildingTextures.get(BuildingType.CASTLE),
                            buildingTextures.get(BuildingType.CASTLE_TOP)
                    };
                }
                return new TextureAtlas.AtlasRegion[]{
                        buildingTextures.get(build.type)
                };
            }
            return new TextureAtlas.AtlasRegion[]{buildingTextures.get(BuildingType.UNKNOWN)};
        } else {
            throw new RuntimeException("Unknown Texture Type");
        }
    }

    public static TextureAtlas.AtlasRegion getHumanTexture(Entity e) {
        if (e.type == EntityType.HUMAN) {
            LifeComponent life = e.getEntityComponent(LifeComponent.class);
            if (humanTextures.containsKey(life.type)) {
                return humanTextures.get(life.type);
            }
            return humanTextures.get(HumanType.MAN);
        } else {
            throw new RuntimeException("Unknown Texture Type");
        }
    }

    public static void setupAtlas(TextureAtlas atlas) {
        humanTextures.put(HumanType.MAN, atlas.findRegion("Unit/medievalUnit", 1));
        humanTextures.put(HumanType.WOMAN, atlas.findRegion("Unit/medievalUnit", 24));

        buildingTextures.put(BuildingType.UNKNOWN, atlas.findRegion("Structure/medievalStructure", 8));
        buildingTextures.put(BuildingType.SMALL_HOUSE, atlas.findRegion("Structure/medievalStructure", 18));
        buildingTextures.put(BuildingType.WOOD_STORE, atlas.findRegion("Structure/medievalStructure", 16));
        buildingTextures.put(BuildingType.CASTLE, atlas.findRegion("Structure/medievalStructure", 6));
        buildingTextures.put(BuildingType.CASTLE_TOP, atlas.findRegion("Structure/medievalStructure", 2));
    }
}
