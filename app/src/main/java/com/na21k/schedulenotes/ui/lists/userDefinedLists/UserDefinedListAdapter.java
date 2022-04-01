package com.na21k.schedulenotes.ui.lists.userDefinedLists;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.databinding.SimpleListItemBinding;
import com.na21k.schedulenotes.ui.shared.viewHolders.BaseViewHolder;

import java.util.List;

public class UserDefinedListAdapter extends RecyclerView.Adapter<UserDefinedListAdapter.UserDefinedListItemViewHolder> {

    private final OnItemActionRequestedListener mOnItemActionRequestedListener;
    private List<UserDefinedListItem> mItems;

    public UserDefinedListAdapter(OnItemActionRequestedListener onItemActionRequestedListener) {
        mOnItemActionRequestedListener = onItemActionRequestedListener;
    }

    @NonNull
    @Override
    public UserDefinedListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SimpleListItemBinding binding = SimpleListItemBinding
                .inflate(inflater, parent, false);

        return new UserDefinedListItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDefinedListItemViewHolder holder, int position) {
        UserDefinedListItem item = mItems.get(position);
        holder.setData(item);
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public void setItems(List<UserDefinedListItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public class UserDefinedListItemViewHolder extends BaseViewHolder {

        private final SimpleListItemBinding mBinding;
        private UserDefinedListItem mItem;

        public UserDefinedListItemViewHolder(SimpleListItemBinding binding) {
            super(binding.getRoot(),
                    R.menu.user_defined_list_item_long_press_menu, 0);
            mBinding = binding;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.item_delete_menu_item:
                    mOnItemActionRequestedListener.onItemDeletionRequested(mItem);
                    return true;
                default:
                    return false;
            }
        }

        private void setData(UserDefinedListItem item) {
            mItem = item;
            mBinding.itemText.setText(item.getText());

            itemView.setOnClickListener(
                    v -> mOnItemActionRequestedListener.onItemUpdateRequested(item));

            itemView.setOnCreateContextMenuListener(this);
        }
    }

    public interface OnItemActionRequestedListener {

        void onItemUpdateRequested(UserDefinedListItem userDefinedListItem);

        void onItemDeletionRequested(UserDefinedListItem userDefinedListItem);
    }
}
