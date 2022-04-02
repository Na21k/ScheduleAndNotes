package com.na21k.schedulenotes.ui.shared.viewHolders;

import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder
        implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

    @MenuRes
    private final int mContextMenuRes;
    @StringRes
    private final int mContextMenuHeaderRes;

    public BaseViewHolder(@NonNull View itemView, @MenuRes int contextMenuRes,
                          @StringRes int contextMenuHeaderRes) {
        super(itemView);
        mContextMenuRes = contextMenuRes;
        mContextMenuHeaderRes = contextMenuHeaderRes;
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if (mContextMenuRes != 0) {
            MenuInflater menuInflater = new MenuInflater(v.getContext());
            menuInflater.inflate(mContextMenuRes, menu);

            if (mContextMenuHeaderRes != 0) {
                menu.setHeaderTitle(mContextMenuHeaderRes);
            }

            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                item.setOnMenuItemClickListener(this);
            }
        }
    }
}
