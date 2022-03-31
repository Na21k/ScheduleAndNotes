package com.na21k.schedulenotes.data.models;

import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;

public class UserDefinedListModel extends UserDefinedList {

    private final int mItemsCount;

    public UserDefinedListModel(UserDefinedList userDefinedList, int itemsCount) {
        super(userDefinedList.getId(), userDefinedList.getTitle());
        mItemsCount = itemsCount;
    }

    public int getItemsCount() {
        return mItemsCount;
    }
}
