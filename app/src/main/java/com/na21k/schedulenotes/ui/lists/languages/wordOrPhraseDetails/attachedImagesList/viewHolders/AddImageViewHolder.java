package com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList.viewHolders;

import android.view.MenuItem;

import com.na21k.schedulenotes.databinding.ImagesListAddImageFooterBinding;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList.OnImageActionRequestedListener;

public class AddImageViewHolder extends ImagesListViewHolderBase {

    public AddImageViewHolder(ImagesListAddImageFooterBinding binding,
                              OnImageActionRequestedListener actionRequestedListener) {
        super(binding.getRoot(), 0, 0, actionRequestedListener);
        itemView.setOnClickListener(v -> mActionRequestedListener.onImageAdditionRequested());
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}
