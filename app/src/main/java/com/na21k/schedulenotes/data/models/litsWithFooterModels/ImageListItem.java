package com.na21k.schedulenotes.data.models.litsWithFooterModels;

import java.io.File;

public class ImageListItem extends ListWithFooterItem {

    private final File mImage;

    public ImageListItem(String imageAbsolutePath) {
        mImage = new File(imageAbsolutePath);
    }

    public File getImage() {
        return mImage;
    }

    @Override
    public int getType() {
        return NORMAL_ITEM;
    }
}
