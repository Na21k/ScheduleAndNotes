package com.na21k.schedulenotes.data.models.groupedListModels;

public class HeaderListItem extends GroupedListsItem {

    private final String mHeaderText;

    public HeaderListItem(String headerText) {
        mHeaderText = headerText;
    }

    public String getHeaderText() {
        return mHeaderText;
    }

    @Override
    public int getType() {
        return HEADER_ITEM;
    }
}
