package com.na21k.schedulenotes.ui.lists.music;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.databinding.SimpleListItemBinding;
import com.na21k.schedulenotes.ui.shared.viewHolders.SimpleListItemViewHolder;

import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicViewHolder> {

    private final OnMusicActionRequestedListener mOnMusicActionRequestedListener;
    private List<MusicListItem> mMusic;

    public MusicListAdapter(OnMusicActionRequestedListener onMusicActionRequestedListener) {
        mOnMusicActionRequestedListener = onMusicActionRequestedListener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SimpleListItemBinding binding = SimpleListItemBinding
                .inflate(inflater, parent, false);

        return new MusicViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        MusicListItem musicListItem = mMusic.get(position);
        holder.setData(musicListItem);
    }

    @Override
    public int getItemCount() {
        return mMusic != null ? mMusic.size() : 0;
    }

    public void setMusic(List<MusicListItem> musicListItems) {
        mMusic = musicListItems;
        notifyDataSetChanged();
    }

    public class MusicViewHolder extends SimpleListItemViewHolder {

        private MusicListItem mMusic;

        public MusicViewHolder(@NonNull View itemView, SimpleListItemBinding binding) {
            super(itemView, binding);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.item_delete_menu_item:
                    mOnMusicActionRequestedListener.onMusicDeletionRequested(mMusic);
                    return true;
                default:
                    return false;
            }
        }

        private void setData(MusicListItem musicListItem) {
            mMusic = musicListItem;
            mBinding.itemText.setText(musicListItem.getText());

            itemView.setOnClickListener(
                    v -> mOnMusicActionRequestedListener.onMusicUpdateRequested(musicListItem));

            itemView.setOnCreateContextMenuListener(this);
        }
    }

    public interface OnMusicActionRequestedListener {

        void onMusicUpdateRequested(MusicListItem musicListItem);

        void onMusicDeletionRequested(MusicListItem musicListItem);
    }
}
