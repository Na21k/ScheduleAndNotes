package com.na21k.schedulenotes.ui.lists.languages;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.LanguagesListItemsSortingOrder;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.models.LanguagesListItemModel;
import com.na21k.schedulenotes.databinding.ActivityLanguagesListBinding;
import com.na21k.schedulenotes.helpers.UiHelper;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.WordOrPhraseDetailsActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LanguagesListActivity extends AppCompatActivity
        implements LanguagesListAdapter.OnLanguagesItemActionRequestedListener {

    private static final int mLandscapeColumnCount = 2;
    private static final int mPortraitColumnCountTablet = 2;
    private static final int mLandscapeColumnCountTablet = 3;
    protected final Observer<List<LanguagesListItem>> mItemsObserver =
            new Observer<List<LanguagesListItem>>() {
                @Override
                public void onChanged(List<LanguagesListItem> items) {
                    mViewModel.setDisplayedItemsCache(items);
                    updateListIfEnoughData();
                }
            };
    protected LanguagesListViewModel mViewModel;
    protected ActivityLanguagesListBinding mBinding;
    private LanguagesListAdapter mListAdapter;
    private LiveData<List<LanguagesListItem>> mLastSearchLiveData;
    private boolean isSearchMode = false;
    private boolean addingItemsEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(LanguagesListViewModel.class);
        mBinding = ActivityLanguagesListBinding.inflate(getLayoutInflater());
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
    protected void onResume() {
        super.onResume();
        //when going back from LanguagesListArchiveActivity
        //in case the sorting order was changed from there
        updateListIfEnoughData();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.languages_list_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                isSearchMode = true;
                mBinding.addWordOrPhraseFab.hide();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                isSearchMode = false;
                if (addingItemsEnabled) mBinding.addWordOrPhraseFab.show();
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
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        switch (getSortingOrder()) {
            case Word:
                menu.findItem(R.id.menu_sort_by_word_or_phrase).setChecked(true);
                break;
            case Translation:
                menu.findItem(R.id.menu_sort_by_translation).setChecked(true);
                break;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        LanguagesListItemsSortingOrder order;

        switch (item.getItemId()) {
            case R.id.menu_archived:
                openArchive();
                break;
            case R.id.menu_sort_by_word_or_phrase:
                order = LanguagesListItemsSortingOrder.Word;
                setSortingOrder(order);
                updateListIfEnoughData();
                break;
            case R.id.menu_sort_by_translation:
                order = LanguagesListItemsSortingOrder.Translation;
                setSortingOrder(order);
                updateListIfEnoughData();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void handleWindowInsets() {
        UiHelper.handleWindowInsets(getWindow(), mBinding.getRoot(),
                mBinding.container, mBinding.wordsAndPhrasesListRecyclerView,
                mBinding.addWordOrPhraseFab, true);
    }

    private void setUpList() {
        RecyclerView recyclerView = mBinding.wordsAndPhrasesListRecyclerView;
        mListAdapter = new LanguagesListAdapter(this);
        recyclerView.setAdapter(mListAdapter);
        LinearLayoutManager layoutManager = UiHelper.getRecyclerViewLayoutManager(this,
                mLandscapeColumnCountTablet, mPortraitColumnCountTablet, mLandscapeColumnCount);
        recyclerView.setLayoutManager(layoutManager);
        setObservers();
    }

    protected void setObservers() {
        mViewModel.getUnarchived().observe(this, mItemsObserver);
        mViewModel.getAllAttachedImagesListItemIds().observe(this, integers -> {
            mViewModel.setDisplayedItemsAttachedImagesListItemIdsCache(integers);
            updateListIfEnoughData();
        });
    }

    private void replaceItemsObserverAccordingToSearchQuery(String query) {
        mViewModel.getUnarchived().removeObservers(this);

        if (mLastSearchLiveData != null) {
            mLastSearchLiveData.removeObservers(this);
        }

        mLastSearchLiveData = mViewModel.getItemsSearch(query);
        mLastSearchLiveData.observe(this, mItemsObserver);
    }

    protected void updateListIfEnoughData() {
        List<LanguagesListItem> itemsCache = mViewModel.getDisplayedItemsCache();
        List<Integer> attachedImagesListItemIdsCache = mViewModel
                .getDisplayedItemsAttachedImagesListItemIdsCache();

        boolean isEnoughData = itemsCache != null && attachedImagesListItemIdsCache != null;

        if (!isEnoughData) {
            return;
        }

        List<LanguagesListItemModel> models = new ArrayList<>();

        for (LanguagesListItem item : itemsCache) {
            int itemId = item.getId();
            int attachedImagesCount = attachedImagesListItemIdsCache.stream()
                    .filter(integer -> integer == itemId).mapToInt(value -> 1).sum();

            models.add(new LanguagesListItemModel(item, attachedImagesCount));
        }

        switch (getSortingOrder()) {
            case Word:
                models.sort(Comparator.comparing(LanguagesListItemModel::getText));
                break;
            case Translation:
                models.sort(Comparator.comparing(LanguagesListItemModel::getTranslation));
                break;
        }

        mListAdapter.setData(models);
    }

    private void setListeners() {
        mBinding.addWordOrPhraseFab.setOnClickListener(v -> addItem());

        mBinding.wordsAndPhrasesListRecyclerView.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (isSearchMode) {
                        return;
                    }

                    if (scrollY <= oldScrollY) {
                        mBinding.addWordOrPhraseFab.extend();
                    } else {
                        mBinding.addWordOrPhraseFab.shrink();
                    }

                    if (!v.canScrollVertically(1) && v.canScrollVertically(-1)) {
                        mBinding.addWordOrPhraseFab.hide();
                    } else {
                        if (addingItemsEnabled) mBinding.addWordOrPhraseFab.show();
                    }
                });
    }

    protected void disableAddingItems() {
        addingItemsEnabled = false;
        mBinding.addWordOrPhraseFab.setVisibility(View.GONE);
    }

    private void openArchive() {
        Intent intent = new Intent(this, LanguagesListArchiveActivity.class);
        startActivity(intent);
    }

    private void addItem() {
        Intent intent = new Intent(this, WordOrPhraseDetailsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemUpdateRequested(LanguagesListItemModel item) {
        Intent intent = new Intent(this, WordOrPhraseDetailsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LANGUAGES_LIST_ITEM_ID_INTENT_KEY, item.getId());
        intent.putExtras(bundle);

        startActivity(intent);
    }

    @Override
    public void onItemToggleArchivingRequested(LanguagesListItemModel item) {
        boolean archive = !item.isArchived();

        if (archive) archiveItem(item);
        else mViewModel.unarchive(item);
    }

    @Override
    public void onItemDeletionRequested(LanguagesListItemModel item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_24);
        builder.setTitle(R.string.list_item_deletion_alert_title);
        builder.setMessage(R.string.list_item_deletion_alert_message);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            Handler deletionHandler = new Handler();
            Runnable deletionRunnable = () -> {
                mViewModel.delete(item);
                UiHelper.showSnackbar(mBinding.getRoot(), R.string.list_item_deleted_snackbar);
            };
            deletionHandler.postDelayed(deletionRunnable,
                    Constants.UNDO_DELETE_TIMEOUT_MILLIS + 300);

            Snackbar.make(mBinding.getRoot(),
                            R.string.list_item_scheduled_for_deletion_snackbar,
                            Constants.UNDO_DELETE_TIMEOUT_MILLIS)
                    .setAction(R.string.cancel,
                            v -> deletionHandler.removeCallbacks(deletionRunnable))
                    .show();
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }

    public void archiveItem(LanguagesListItemModel item) {
        mViewModel.isArchiveEmpty().addOnSuccessListener(isEmpty -> {
            if (!isEmpty) {
                mViewModel.archive(item);
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_archive_24);
            builder.setTitle(R.string.first_time_archive_explanation_alert_title);
            builder.setMessage(R.string.first_time_archive_explanation_alert_message);

            builder.setPositiveButton(android.R.string.ok,
                    (dialog, which) -> mViewModel.archive(item));
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            });

            builder.show();
        });
    }

    private LanguagesListItemsSortingOrder getSortingOrder() {
        String orderStr = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Constants.LANGUAGES_LIST_SORTING_ORDER_PREFERENCE_KEY,
                        LanguagesListItemsSortingOrder.Word.toString());
        return LanguagesListItemsSortingOrder.valueOf(orderStr);
    }

    private void setSortingOrder(LanguagesListItemsSortingOrder sortingOrder) {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString(Constants.LANGUAGES_LIST_SORTING_ORDER_PREFERENCE_KEY,
                        sortingOrder.toString()).apply();
    }
}
