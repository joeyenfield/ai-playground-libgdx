package com.emptypocketstudios.boardgame.engine.entity.components.human;

import com.badlogic.gdx.math.MathUtils;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.EntityComponent;

public class LifeComponent extends EntityComponent {
    public float health = 0;
    public float hunger = 0;
    public float thirst = 0;
    public float energy = 0;
    public float age = 0;

    public HumanType type = HumanType.MAN;
    float hungerRate = 100f / (60*2);
    float thirstRate = 100f / (60*2);
    float energyRate = 100f / (60*2);

    public LifeComponent(Entity entity) {
        super(entity);
    }

    public LifeComponent setType(HumanType type) {
        entity.removeTag("humanType:" + this.type.name());
        this.type = type;
        entity.addTag("humanType:" + this.type.name());
        return this;
    }

    @Override
    public void update(float delta) {
        age += delta;
        hunger -= hungerRate * delta;
        thirst -= thirstRate * delta;
        energy -= energyRate * delta;

        hunger = MathUtils.clamp(hunger, 0, 100);
        thirst = MathUtils.clamp(thirst, 0, 100);
        energy = MathUtils.clamp(energy, 0, 100);

    }


}
