package com.na21k.schedulenotes.ui.lists;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.models.UserDefinedListModel;
import com.na21k.schedulenotes.databinding.ListsListItemBinding;

import java.util.List;

public class ListsListAdapter extends RecyclerView.Adapter<ListsListAdapter.ListViewHolder> {

    private final OnListActionRequestedListener mOnListActionRequestedListener;
    private List<UserDefinedListModel> mLists;

    public ListsListAdapter(OnListActionRequestedListener onListActionRequestedListener) {
        mOnListActionRequestedListener = onListActionRequestedListener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListsListItemBinding binding = ListsListItemBinding
                .inflate(inflater, parent, false);

        return new ListViewHolder(binding.getRoot(), binding);
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

    public class ListViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private final ListsListItemBinding mBinding;
        private UserDefinedListModel mList;

        public ListViewHolder(@NonNull View itemView, ListsListItemBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater menuInflater = new MenuInflater(v.getContext());
            menuInflater.inflate(R.menu.list_long_press_menu, menu);

            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                item.setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
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
            mBinding.listItemsCount.setText(list.getItemsCount());

            itemView.setOnClickListener(
                    v -> mOnListActionRequestedListener.onListOpenRequested(list));

            itemView.setOnCreateContextMenuListener(this);
        }
    }

    public interface OnListActionRequestedListener {

        void onListOpenRequested(UserDefinedList list);

        void onListDeletionRequested(UserDefinedList list);
    }
}
