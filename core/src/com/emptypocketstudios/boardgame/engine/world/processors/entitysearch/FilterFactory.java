package com.emptypocketstudios.boardgame.engine.world.processors.entitysearch;

public class FilterFactory {
    public static final String EXPRESSION_START = "(";
    public static final String EXPRESSION_END = ")";

    public static final String EXPRESSION_AND = "AND";
    public static final String EXPRESSION_OR = "OR";

    public static final String EXPRESSION_TAG = "tag";
    public static final String EXPRESSION_NAME = "name";


    public EntityFilter basicAndTag(String tags) {
        EntityAndFilter filter = new EntityAndFilter();
        for (String tag : tags.split(" ")) {
            if (tag.trim().length() > 0) {
                filter.add(new EntityTagFilter().setTag(tag.trim()));
            }
        }
        return filter;
    }

    public EntityFilter basicOrTag(String tags) {
        EntityOrFilter filter = new EntityOrFilter();
        for (String tag : tags.split(" ")) {
            if (tag.trim().length() > 0) {
                filter.add(new EntityTagFilter().setTag(tag.trim()));
            }
        }
        return filter;
    }

}
