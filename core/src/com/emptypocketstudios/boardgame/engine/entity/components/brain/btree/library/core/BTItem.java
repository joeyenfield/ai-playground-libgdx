package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.library.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.emptypocketstudios.boardgame.engine.entity.Entity;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.Command;
import com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser.ParseException;

public abstract class BTItem implements Pool.Poolable {
    public String name = null;
    protected Entity entity;
    BTItem parent = null;
    protected boolean debugResult = false;
    protected boolean debugTrace = false;
    protected boolean debugLog = false;

    BTResult forcedResult = null;

    protected boolean debugResultSetting = false;
    protected boolean debugTraceSetting = false;
    protected boolean debugLogSetting = false;


    int depth = 0;
    String padding;


    public Array<BTCondition> conditions = new Array<>();

    public BTItem() {
    }

    public abstract String getCommandName();

    public abstract void before();

    public abstract void after();

    public abstract BTResult process();

    protected abstract void parse(Command command);

    public abstract BTItem getChild(int index);

    public abstract int getChildCount();

    public void setup(Command command) {
        if (!getCommandName().equalsIgnoreCase(command.getCommand())) {
            throw new ParseException("Unexpected Command [" + command.getCommand() + " - " + getCommandName() + "]", command);
        }
        this.parse(command);
        debugResultSetting = command.getArgBoolean("debugResult", false);
        debugTraceSetting = command.getArgBoolean("debugTrace", false);
        debugLogSetting = command.getArgBoolean("debugLog", false);

        String forcedResultString = command.getArg("forcedResult", null);
        if (forcedResultString == null) {
            forcedResult = null;
        } else {
            forcedResult = BTResult.valueOf(forcedResultString);
        }
    }

    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setParent(BTItem parent) {
        this.parent = parent;

    }

    public void init() {
        if (this.parent != null) {
            this.depth = this.parent.depth + 1;
            this.debugResult = this.parent.debugResult || debugResultSetting;
            this.debugTrace = this.parent.debugTrace || debugTraceSetting;
            this.debugLog = this.parent.debugLog || debugLogSetting;
        } else {
            this.depth = 0;
        }
        StringBuilder padding = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            padding.append("  ");
        }
        this.padding = padding.toString();
    }

    protected void logDebug(String message) {
        entity.world.engine.log("BTREE", entity, padding + getCommandName()+" Name:[" + name + "] : " + message);
    }

    public BTResult tick() {
        if (debugTrace) logDebug("TICK - " + getClass().getSimpleName());

        // Checks if pre-condition is met before entering
        for (int i = 0; i < conditions.size; i++) {
            BTCondition condition = conditions.get(i);
            if (condition != null && !condition.checkPreCondition(this)) {
                if (debugTrace)
                    logDebug("Condition Failed " + condition.getClass().getSimpleName());
                return BTResult.FAILURE;
            }
        }

        BTResult result = process();
        if (forcedResult != null) {
            if (debugTrace)
                logDebug(" Force Result - Original:" + result + " - Final:" + forcedResult);
            result = forcedResult;
        }
        if (debugTrace || debugResult) logDebug("Result - " + result);
        return result;
    }

    public void printStructure() {
        logDebug("" + depth);
    }

    @Override
    public void reset() {
        Pools.freeAll(conditions);
        conditions.clear();
        parent = null;
        entity = null;
        debugResult = false;
        debugTrace = false;
        forcedResult = null;
    }
}
