package com.na21k.schedulenotes.data.models.litsWithFooterModels;

import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;

public class ImageListItem extends ListWithFooterItem {

    private LanguagesListItemAttachedImage mAttachedImage;

    public ImageListItem(LanguagesListItemAttachedImage attachedImage) {
        mAttachedImage = attachedImage;
    }

    public LanguagesListItemAttachedImage getAttachedImage() {
        return mAttachedImage;
    }

    @Override
    public int getType() {
        return NORMAL_ITEM;
    }
}
