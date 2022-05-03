package com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;
import com.na21k.schedulenotes.data.models.litsWithFooterModels.FooterListItem;
import com.na21k.schedulenotes.data.models.litsWithFooterModels.ImageListItem;
import com.na21k.schedulenotes.data.models.litsWithFooterModels.ListWithFooterItem;
import com.na21k.schedulenotes.databinding.ImagesListAddImageFooterBinding;
import com.na21k.schedulenotes.databinding.ImagesListItemBinding;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList.viewHolders.AddImageViewHolder;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList.viewHolders.ImageItemViewHolder;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList.viewHolders.ImagesListViewHolderBase;

import java.util.ArrayList;
import java.util.List;

public class AttachedImagesListAdapter extends RecyclerView.Adapter<ImagesListViewHolderBase> {

    private final OnImageActionRequestedListener mOnImageActionRequestedListener;
    private List<ListWithFooterItem> mImages;

    public AttachedImagesListAdapter(OnImageActionRequestedListener onImageActionRequestedListener) {
        mOnImageActionRequestedListener = onImageActionRequestedListener;
        mImages = new ArrayList<>();
        mImages.add(new FooterListItem());
        setStateRestorationPolicy(StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }

    @NonNull
    @Override
    public ImagesListViewHolderBase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == ListWithFooterItem.FOOTER_ITEM) {
            ImagesListAddImageFooterBinding binding = ImagesListAddImageFooterBinding
                    .inflate(inflater, parent, false);

            return new AddImageViewHolder(binding, mOnImageActionRequestedListener);
        } else {
            ImagesListItemBinding binding = ImagesListItemBinding
                    .inflate(inflater, parent, false);

            return new ImageItemViewHolder(binding, mOnImageActionRequestedListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesListViewHolderBase holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType != ListWithFooterItem.FOOTER_ITEM) {
            ImageListItem imageListItem = (ImageListItem) mImages.get(position);
            ImageItemViewHolder viewHolder = (ImageItemViewHolder) holder;

            viewHolder.setData(imageListItem.getAttachedImage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mImages.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mImages != null ? mImages.size() : 0;
    }

    public void setData(List<LanguagesListItemAttachedImage> attachedImages) {
        List<ListWithFooterItem> items = new ArrayList<>();

        for (LanguagesListItemAttachedImage image : attachedImages) {
            items.add(new ImageListItem(image));
        }

        items.add(new FooterListItem());

        mImages = items;
        notifyDataSetChanged();
    }

    public void removeItem(LanguagesListItemAttachedImage attachedImage) {
        List<ImageListItem> imagesItemOnly = getImageItemsOnly();

        ImageListItem found = imagesItemOnly.stream()
                .filter(imageListItem -> imageListItem.getAttachedImage() == attachedImage)
                .findFirst().orElse(null);

        if (found != null) {
            int position = mImages.indexOf(found);
            mImages.remove(found);
            notifyItemRemoved(position);
        }
    }

    public void addItem(LanguagesListItemAttachedImage image) {
        ImageListItem model = new ImageListItem(image);
        int position = mImages.size() - 1;    //insert before footer
        mImages.add(position, model);
        notifyItemInserted(position);
    }

    private List<ImageListItem> getImageItemsOnly() {
        List<ImageListItem> res = new ArrayList<>();

        for (int i = 0; i < mImages.size() - 1; i++) {
            res.add((ImageListItem) mImages.get(i));
        }

        return res;
    }
}
