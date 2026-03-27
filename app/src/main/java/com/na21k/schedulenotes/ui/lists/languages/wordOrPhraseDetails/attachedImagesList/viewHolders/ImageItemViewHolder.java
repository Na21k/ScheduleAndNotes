package com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList.viewHolders;

import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.databinding.ImagesListItemBinding;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList.OnImageActionRequestedListener;

import java.io.File;

public class ImageItemViewHolder extends ImagesListViewHolderBase {

    private final ImagesListItemBinding mBinding;
    private File mAttachedImage;

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

    public void setData(File image) {
        mAttachedImage = image;

        String signature = "" + image.lastModified() + image.length();

        Glide.with(itemView)
                .load(image)
                .signature(new ObjectKey(signature))
                .placeholder(R.drawable.ic_image_24)
                .error(R.drawable.ic_error_24)
                .into(mBinding.imageView);
    }
}
