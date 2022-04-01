package com.na21k.schedulenotes.data.models.groupedListModels;

import android.view.MenuItem;

import com.na21k.schedulenotes.databinding.GroupedListHeaderItemBinding;

public class HeaderViewHolder extends GroupedListItemViewHolderBase {

    private final GroupedListHeaderItemBinding mBinding;

    public HeaderViewHolder(GroupedListHeaderItemBinding binding) {
        super(binding.getRoot(), 0, 0);
        mBinding = binding;
    }

    public void setHeaderText(String headerText) {
        mBinding.groupedListHeaderText.setText(headerText);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}
