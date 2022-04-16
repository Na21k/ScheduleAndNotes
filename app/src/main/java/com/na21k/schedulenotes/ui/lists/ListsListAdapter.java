package com.na21k.schedulenotes.ui.lists;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.models.UserDefinedListModel;
import com.na21k.schedulenotes.databinding.ListsListItemBinding;
import com.na21k.schedulenotes.ui.shared.viewHolders.BaseViewHolder;

import java.util.List;

public class ListsListAdapter extends RecyclerView.Adapter<ListsListAdapter.ListViewHolder> {

    private final OnListActionRequestedListener mOnListActionRequestedListener;
    private List<UserDefinedListModel> mLists;

    public ListsListAdapter(OnListActionRequestedListener onListActionRequestedListener) {
        mOnListActionRequestedListener = onListActionRequestedListener;
        setStateRestorationPolicy(StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListsListItemBinding binding = ListsListItemBinding
                .inflate(inflater, parent, false);

        return new ListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        UserDefinedListModel list = mLists.get(position);
        holder.setData(list);
    }

    @Override
    public int getItemCount() {
        return mLists != null ? mLists.size() : 0;
    }

    public void setLists(List<UserDefinedListModel> lists) {
        mLists = lists;
        notifyDataSetChanged();
    }

    public class ListViewHolder extends BaseViewHolder {

        private final ListsListItemBinding mBinding;
        private UserDefinedListModel mList;

        public ListViewHolder(ListsListItemBinding binding) {
            super(binding.getRoot(), R.menu.list_long_press_menu, R.string.list_context_menu_title);
            mBinding = binding;

            itemView.setOnClickListener(
                    v -> mOnListActionRequestedListener.onListOpenRequested(mList));
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.list_rename_menu_item:
                    mOnListActionRequestedListener.onListRenameRequested(mList);
                    return true;
                case R.id.list_delete_menu_item:
                    mOnListActionRequestedListener.onListDeletionRequested(mList);
                    return true;
                default:
                    return false;
            }
        }

        private void setData(@NonNull UserDefinedListModel list) {
            mList = list;
            mBinding.listName.setText(list.getTitle());
            mBinding.listItemsCount.setText(String.valueOf(list.getItemsCount()));
        }
    }

    public interface OnListActionRequestedListener {

        void onListOpenRequested(UserDefinedList list);

        void onListRenameRequested(UserDefinedList list);

        void onListDeletionRequested(UserDefinedList list);
    }
}
