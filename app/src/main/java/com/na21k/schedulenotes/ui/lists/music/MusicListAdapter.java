package com.na21k.schedulenotes.ui.lists.music;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.databinding.SimpleListItemBinding;
import com.na21k.schedulenotes.ui.shared.viewHolders.BaseViewHolder;

import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicViewHolder> {

    private final OnMusicActionRequestedListener mOnMusicActionRequestedListener;
    private List<MusicListItem> mMusic;

    public MusicListAdapter(OnMusicActionRequestedListener onMusicActionRequestedListener) {
        mOnMusicActionRequestedListener = onMusicActionRequestedListener;
        setStateRestorationPolicy(StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SimpleListItemBinding binding = SimpleListItemBinding
                .inflate(inflater, parent, false);

        return new MusicViewHolder(binding);
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

    public class MusicViewHolder extends BaseViewHolder {

        protected final SimpleListItemBinding mBinding;
        private MusicListItem mMusic;

        public MusicViewHolder(SimpleListItemBinding binding) {
            super(binding.getRoot(), R.menu.simple_item_long_press_menu, 0);
            mBinding = binding;

            itemView.setOnClickListener(
                    v -> mOnMusicActionRequestedListener.onMusicUpdateRequested(mMusic));
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
        }
    }

    public interface OnMusicActionRequestedListener {

        void onMusicUpdateRequested(MusicListItem musicListItem);

        void onMusicDeletionRequested(MusicListItem musicListItem);
    }
}
