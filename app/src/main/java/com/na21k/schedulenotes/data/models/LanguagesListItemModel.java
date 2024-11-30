package com.na21k.schedulenotes.data.models;

import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;

public class LanguagesListItemModel extends LanguagesListItem {

    private final int mAttachedImagesCount;

    public LanguagesListItemModel(LanguagesListItem languagesListItem, int attachedImagesCount) {
        super(languagesListItem);
        mAttachedImagesCount = attachedImagesCount;
    }

    public int getAttachedImagesCount() {
        return mAttachedImagesCount;
    }
}
