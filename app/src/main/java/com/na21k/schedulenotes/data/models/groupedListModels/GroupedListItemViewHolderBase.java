package com.na21k.schedulenotes.data.models.groupedListModels;

import android.view.View;

import androidx.annotation.NonNull;

import com.na21k.schedulenotes.ui.shared.viewHolders.BaseViewHolder;

public abstract class GroupedListItemViewHolderBase extends BaseViewHolder {

    public GroupedListItemViewHolderBase(@NonNull View itemView, int contextMenuRes, int contextMenuHeaderRes) {
        super(itemView, contextMenuRes, contextMenuHeaderRes);
    }
}
