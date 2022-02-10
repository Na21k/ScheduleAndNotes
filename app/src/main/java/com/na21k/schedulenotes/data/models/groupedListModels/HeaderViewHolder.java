package com.na21k.schedulenotes.data.models.groupedListModels;

import android.view.View;

import androidx.annotation.NonNull;

import com.na21k.schedulenotes.databinding.GroupedListHeaderItemBinding;

public class HeaderViewHolder extends GroupedListItemViewHolderBase {

    private final GroupedListHeaderItemBinding mBinding;

    public HeaderViewHolder(@NonNull View itemView, GroupedListHeaderItemBinding binding) {
        super(itemView);
        mBinding = binding;
    }

    public void setHeaderText(String headerText) {
        mBinding.groupedListHeaderText.setText(headerText);
    }
}
