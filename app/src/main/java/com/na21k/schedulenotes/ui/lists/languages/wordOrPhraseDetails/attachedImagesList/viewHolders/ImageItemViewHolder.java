package com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList.viewHolders;

import android.view.MenuItem;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;
import com.na21k.schedulenotes.databinding.ImagesListItemBinding;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList.OnImageActionRequestedListener;

public class ImageItemViewHolder extends ImagesListViewHolderBase {

    private final ImagesListItemBinding mBinding;
    private LanguagesListItemAttachedImage mAttachedImage;

    public ImageItemViewHolder(ImagesListItemBinding binding,
                               OnImageActionRequestedListener actionRequestedListener) {
        super(binding.getRoot(), R.menu.image_long_press_menu, 0,
                actionRequestedListener);
        mBinding = binding;
        itemView.setOnClickListener(
                v -> mActionRequestedListener.onImageOpenRequested(mAttachedImage));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.image_delete_menu_item) {
            mActionRequestedListener.onImageDeletionRequested(mAttachedImage);
        }

        return false;
    }

    public void setData(LanguagesListItemAttachedImage image) {
        mAttachedImage = image;
        mBinding.imageView.setImageBitmap(image.getThumbnailBitmap());
    }
}
