package com.emptypocketstudios.boardgame.engine.entity.components.building;

import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.EntityComponent;
import com.emptypocketstudios.boardgame.engine.world.Cell;
import com.emptypocketstudios.boardgame.engine.world.CellType;

public class BuildingComponent extends EntityComponent {
    public BuildingType type;

    public BuildingComponent(Entity entity) {
        super(entity);
    }

    public void placeBuilding() {
        Cell cell = entity.getCurrentCell();
        cell.setType(CellType.BUILDING_FOUNDATION);
        cell.isRoad = true;

        Cell nextCell = cell.getLink(0, -1);
        if (nextCell != null) {
            nextCell.setType(CellType.GRASS);
            nextCell.isRoad = true;
        }
    }

    @Override
    public void update(float delta) {

    }

    public void setType(BuildingType type) {
        entity.removeTag("buildingType:" + type.name());
        this.type = type;
        entity.addTag("buildingType:" + type.name());
    }
}
