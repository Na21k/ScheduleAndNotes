package com.na21k.schedulenotes.ui.lists.music;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.databinding.ActivityMusicListBinding;
import com.na21k.schedulenotes.databinding.MusicInfoAlertViewBinding;
import com.na21k.schedulenotes.helpers.UiHelper;

import java.util.Comparator;

public class MusicListActivity extends AppCompatActivity
        implements MusicListAdapter.OnMusicActionRequestedListener {

    private MusicListViewModel mViewModel;
    private ActivityMusicListBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(MusicListViewModel.class);
        mBinding = ActivityMusicListBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setUpList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setUpList() {
        RecyclerView recyclerView = mBinding.includedList.simpleList;
        MusicListAdapter adapter = new MusicListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        observeMusic(adapter);
        setListeners();
    }

    private void observeMusic(MusicListAdapter adapter) {
        mViewModel.getAll().observe(this, musicListItems -> {
            musicListItems.sort(Comparator.comparing(MusicListItem::getText));
            adapter.setMusic(musicListItems);
        });
    }

    private void setListeners() {
        mBinding.addMusicFab.setOnClickListener(v -> newMusic());

        mBinding.includedList.simpleList.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY <= oldScrollY) {
                        mBinding.addMusicFab.extend();
                    } else {
                        mBinding.addMusicFab.shrink();
                    }

                    if (!v.canScrollVertically(1) && v.canScrollVertically(-1)) {
                        mBinding.addMusicFab.hide();
                    } else {
                        mBinding.addMusicFab.show();
                    }
                });
    }

    private void newMusic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_add_24);
        builder.setTitle(R.string.list_item_creation_alert_title);

        MusicInfoAlertViewBinding viewBinding = MusicInfoAlertViewBinding
                .inflate(getLayoutInflater(), mBinding.getRoot(), false);
        viewBinding.input.requestFocus();
        builder.setView(viewBinding.getRoot());

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            Editable musicTextEditable = viewBinding.input.getText();

            if (musicTextEditable != null && !musicTextEditable.toString().isEmpty()) {
                String musicText = musicTextEditable.toString();
                mViewModel.addNew(new MusicListItem(0, musicText));
            } else {
                newMusic();
                UiHelper.showErrorDialog(this,
                        R.string.list_item_creation_empty_input_alert_message);
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });

        builder.show();
    }

    @Override
    public void onMusicUpdateRequested(MusicListItem musicListItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_edit_24);
        builder.setTitle(R.string.list_item_editing_alert_title);

        MusicInfoAlertViewBinding viewBinding = MusicInfoAlertViewBinding
                .inflate(getLayoutInflater(), mBinding.getRoot(), false);
        viewBinding.input.setText(musicListItem.getText());
        viewBinding.input.requestFocus();
        builder.setView(viewBinding.getRoot());

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            Editable musicTextEditable = viewBinding.input.getText();

            if (musicTextEditable != null && !musicTextEditable.toString().isEmpty()) {
                String musicText = musicTextEditable.toString();
                musicListItem.setText(musicText);
                mViewModel.update(musicListItem);
            } else {
                onMusicUpdateRequested(musicListItem);
                UiHelper.showErrorDialog(this,
                        R.string.list_item_editing_empty_input_alert_message);
            }
        });
        builder.setNeutralButton(R.string.cancel, (dialog, which) -> {
        });
        builder.setNegativeButton(R.string.delete,
                (dialog, which) -> onMusicDeletionRequested(musicListItem));

        builder.show();
    }

    @Override
    public void onMusicDeletionRequested(MusicListItem musicListItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_24);
        builder.setTitle(R.string.list_item_deletion_alert_title);
        builder.setMessage(R.string.list_item_deletion_alert_message);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            mViewModel.delete(musicListItem);
            Snackbar.make(mBinding.getRoot(), R.string.list_item_deleted_snackbar, 3000).show();
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }
}
