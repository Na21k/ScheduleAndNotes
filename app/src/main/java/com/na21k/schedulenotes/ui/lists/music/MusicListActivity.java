package com.na21k.schedulenotes.ui.lists.music;

import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.ScheduleNotesApplication;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.databinding.ActivityMusicListBinding;
import com.na21k.schedulenotes.databinding.MusicInfoAlertViewBinding;
import com.na21k.schedulenotes.helpers.UiHelper;

import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

public class MusicListActivity extends AppCompatActivity
        implements MusicListAdapter.OnMusicActionRequestedListener {

    private static final int mLandscapeColumnCount = 2;
    private static final int mPortraitColumnCountTablet = 2;
    private static final int mLandscapeColumnCountTablet = 3;
    private final Observer<List<MusicListItem>> mItemsObserver = new Observer<List<MusicListItem>>() {
        @Override
        public void onChanged(List<MusicListItem> musicListItems) {
            musicListItems.sort(Comparator.comparing(MusicListItem::getText));
            mListAdapter.setMusic(musicListItems);
        }
    };
    @Inject
    protected MusicListViewModel.Factory mViewModelFactory;
    private MusicListViewModel mViewModel;
    private ActivityMusicListBinding mBinding;
    private MusicListAdapter mListAdapter;
    private LiveData<List<MusicListItem>> mLastSearchLiveData;
    private boolean isSearchMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((ScheduleNotesApplication) getApplicationContext())
                .getAppComponent()
                .inject(this);
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, mViewModelFactory).get(MusicListViewModel.class);
        mBinding = ActivityMusicListBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar.appBar);

        handleWindowInsets();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setUpList();
        setListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.simple_list_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                isSearchMode = true;
                mBinding.addMusicFab.hide();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                isSearchMode = false;
                mBinding.addMusicFab.show();
                return true;
            }
        });

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                replaceItemsObserverAccordingToSearchQuery(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void handleWindowInsets() {
        UiHelper.handleWindowInsets(getWindow(), mBinding.getRoot(),
                mBinding.container, mBinding.includedList.simpleList, mBinding.addMusicFab,
                true);
    }

    private void setUpList() {
        RecyclerView recyclerView = mBinding.includedList.simpleList;
        mListAdapter = new MusicListAdapter(this);
        recyclerView.setAdapter(mListAdapter);
        LinearLayoutManager layoutManager = UiHelper.getRecyclerViewLayoutManager(this,
                mLandscapeColumnCountTablet, mPortraitColumnCountTablet, mLandscapeColumnCount);
        recyclerView.setLayoutManager(layoutManager);
        observeMusic();
    }

    private void observeMusic() {
        mViewModel.getAll().observe(this, mItemsObserver);
    }

    private void replaceItemsObserverAccordingToSearchQuery(String query) {
        mViewModel.getAll().removeObservers(this);

        if (mLastSearchLiveData != null) {
            mLastSearchLiveData.removeObservers(this);
        }

        mLastSearchLiveData = mViewModel.getItemsSearch(query);
        mLastSearchLiveData.observe(this, mItemsObserver);
    }

    private void setListeners() {
        mBinding.addMusicFab.setOnClickListener(v -> newMusic());

        mBinding.includedList.simpleList.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (isSearchMode) {
                        return;
                    }

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

        AlertDialog alertDialog = builder.show();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });
        builder.setNeutralButton(R.string.delete,
                (dialog, which) -> onMusicDeletionRequested(musicListItem));

        AlertDialog alertDialog = builder.show();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onMusicDeletionRequested(MusicListItem musicListItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_24);
        builder.setTitle(R.string.list_item_deletion_alert_title);
        builder.setMessage(R.string.list_item_deletion_alert_message);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            mViewModel.delete(musicListItem);
            Snackbar.make(mBinding.getRoot(),
                            R.string.list_item_deleted_snackbar,
                            Constants.UNDO_DELETE_TIMEOUT_MILLIS)
                    .setAction(R.string.undo, v -> mViewModel.addNew(musicListItem))
                    .show();
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }
}
