package com.emptypocketstudios.boardgame.engine.entity.components.building;

public enum BuildingType {
    UNKNOWN(1, 0, 0),
    SMALL_HOUSE(1, 0, 0),
    WOOD_STORE(1, 0, 0),
    CASTLE(1, 0, 0),
    CASTLE_TOP(1, 0, 0);

    int wood = 0;
    int stone = 0;
    int gold = 0;

    BuildingType(int wood, int stone, int gold) {
        this.wood = wood;
        this.stone = stone;
        this.gold = gold;
    }
}
