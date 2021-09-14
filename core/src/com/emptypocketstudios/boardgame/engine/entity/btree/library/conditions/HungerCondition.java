package com.emptypocketstudios.boardgame.engine.entity.btree.library.conditions;

import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.btree.BTCondition;
import com.emptypocketstudios.boardgame.engine.entity.btree.BTItem;
import com.emptypocketstudios.boardgame.engine.entity.btree.UnparseableLineException;
import com.emptypocketstudios.boardgame.engine.entity.components.HungerComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HungerCondition implements BTCondition {
    private static Pattern ptn =
            Pattern.compile("HUNGER \"([^\"]*)\"");
    float hungerThreshold = 0;

    @Override
    public boolean checkPreCondition(BTItem parent) {
        Entity entity = (Entity) parent.memory.get("ENTITY");
        HungerComponent hungerComponent = entity.getEntityComponent(HungerComponent.class);
        if(hungerComponent != null){
            if(hungerComponent.hunger > this.hungerThreshold){
                return true;
            }
        }
        return false;
    }

    @Override
    public void parse(String command) {
        Matcher match = ptn.matcher(command);
        try {
            if (match.matches()) {
                String hungerLevel = match.group(1);
                this.hungerThreshold = Float.parseFloat(hungerLevel);
            } else {
                throw new RuntimeException("Did not match expected : " + ptn.pattern());
            }
        } catch (Exception e) {
            throw new UnparseableLineException("Did not match expected : " + ptn.pattern(), e);
        }
    }

    public static void main(String[] args) {
        HungerCondition condition = new HungerCondition();
        condition.parse("HUNGER \"0.5\"");
    }
}
