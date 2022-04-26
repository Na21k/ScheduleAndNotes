package com.na21k.schedulenotes.ui.lists.languages;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.databinding.LanguagesListItemBinding;
import com.na21k.schedulenotes.ui.shared.viewHolders.BaseViewHolder;

import java.util.List;

public class LanguagesListAdapter extends RecyclerView.Adapter<LanguagesListAdapter.WordViewHolder> {

    private final OnLanguagesItemActionRequestedListener mOnLanguagesItemActionRequestedListener;
    private List<LanguagesListItem> mItems;

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
        LanguagesListItem item = mItems.get(position);
        holder.setData(item);
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public void setData(List<LanguagesListItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public class WordViewHolder extends BaseViewHolder {

        private final LanguagesListItemBinding mBinding;
        private LanguagesListItem mItem;

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

        private void setData(LanguagesListItem item) {
            mItem = item;
            mBinding.languagesListItemWordOrPhrase.setText(item.getText());
            mBinding.languagesListItemTranslation.setText(item.getTranslation());
            mBinding.languagesListItemExplanation.setText(item.getExplanation());
            mBinding.languagesListItemAttachedImagesCount.setText("TODO");
        }
    }

    public interface OnLanguagesItemActionRequestedListener {

        void onItemUpdateRequested(LanguagesListItem item);

        void onItemDeletionRequested(LanguagesListItem item);
    }
}
