package com.na21k.schedulenotes.data.models.groupedListModels;

public abstract class GroupedListsItem {

    public static final int HEADER_ITEM = 1111;
    public static final int NON_HEADER_ITEM = 1112;

    public abstract int getType();
}
