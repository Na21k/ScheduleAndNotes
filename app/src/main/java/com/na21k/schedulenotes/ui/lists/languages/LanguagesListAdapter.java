package com.na21k.schedulenotes.ui.lists.languages;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.models.LanguagesListItemModel;
import com.na21k.schedulenotes.databinding.LanguagesListItemBinding;
import com.na21k.schedulenotes.ui.shared.viewHolders.BaseViewHolder;

import java.util.List;

public class LanguagesListAdapter extends RecyclerView.Adapter<LanguagesListAdapter.WordViewHolder> {

    private final OnLanguagesItemActionRequestedListener mOnLanguagesItemActionRequestedListener;
    private List<LanguagesListItemModel> mItems;

    public LanguagesListAdapter(OnLanguagesItemActionRequestedListener onLanguagesItemActionRequestedListener) {
        mOnLanguagesItemActionRequestedListener = onLanguagesItemActionRequestedListener;
        setStateRestorationPolicy(StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LanguagesListItemBinding binding = LanguagesListItemBinding
                .inflate(inflater, parent, false);

        return new WordViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        LanguagesListItemModel item = mItems.get(position);
        holder.setData(item);
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public void setData(List<LanguagesListItemModel> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public class WordViewHolder extends BaseViewHolder {

        private final LanguagesListItemBinding mBinding;
        private LanguagesListItemModel mItem;

        public WordViewHolder(LanguagesListItemBinding binding) {
            super(binding.getRoot(), R.menu.simple_item_long_press_menu, 0);
            mBinding = binding;

            itemView.setOnClickListener(v ->
                    mOnLanguagesItemActionRequestedListener.onItemUpdateRequested(mItem));
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.item_delete_menu_item:
                    mOnLanguagesItemActionRequestedListener.onItemDeletionRequested(mItem);
                    return true;
                default:
                    return false;
            }
        }

        private void setData(LanguagesListItemModel item) {
            mItem = item;
            mBinding.languagesListItemWordOrPhrase.setText(item.getText());
            mBinding.languagesListItemTranscription.setText(item.getTranscription());
            mBinding.languagesListItemTranslation.setText(item.getTranslation());
            mBinding.languagesListItemExplanation.setText(item.getExplanation());
            mBinding.languagesListItemAttachedImagesCount
                    .setText(String.valueOf(item.getAttachedImagesCount()));

            updateViewsVisibility();
        }

        private void updateViewsVisibility() {
            boolean showTranscription = !mItem.getTranscription().isEmpty();
            boolean showTranslation = !mItem.getTranslation().isEmpty();
            boolean showExplanation = !mItem.getExplanation().isEmpty();
            boolean showAttachedImagesCount = mItem.getAttachedImagesCount() > 0;

            mBinding.languagesListItemTranscription
                    .setVisibility(showTranscription ? View.VISIBLE : View.GONE);
            mBinding.languagesListItemTranslation
                    .setVisibility(showTranslation ? View.VISIBLE : View.GONE);
            mBinding.languagesListItemExplanation
                    .setVisibility(showExplanation ? View.VISIBLE : View.GONE);
            mBinding.languagesListItemAttachedImagesCount
                    .setVisibility(showAttachedImagesCount ? View.VISIBLE : View.GONE);
        }
    }

    public interface OnLanguagesItemActionRequestedListener {

        void onItemUpdateRequested(LanguagesListItemModel item);

        void onItemDeletionRequested(LanguagesListItemModel item);
    }
}
