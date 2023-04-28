package com.na21k.schedulenotes.ui.lists.languages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private final Observer<List<LanguagesListItem>> mItemsObserver = new Observer<List<LanguagesListItem>>() {
        @Override
        public void onChanged(List<LanguagesListItem> items) {
            mViewModel.setAllItemsCache(items);
            updateListIfEnoughData();
        }
    };
    private LanguagesListViewModel mViewModel;
    private ActivityLanguagesListBinding mBinding;
    private LanguagesListAdapter mListAdapter;
    private LiveData<List<LanguagesListItem>> mLastSearchLiveData;
    private boolean isSearchMode = false;
    private int mMostRecentBottomInset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(LanguagesListViewModel.class);
        mBinding = ActivityLanguagesListBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar.appBar);

        makeNavBarLookNice();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setUpList();
        setListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.languages_list_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                isSearchMode = true;
                mBinding.addWordOrPhraseFab.hide();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                isSearchMode = false;
                mBinding.addWordOrPhraseFab.show();
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

    private void makeNavBarLookNice() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.getRoot(), (v, insets) -> {
            Insets i = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());

            mBinding.container.setPadding(i.left, i.top, i.right, 0);

            CoordinatorLayout.LayoutParams newFabParams = UiHelper.generateNewFabLayoutParams(
                    this,
                    mBinding.addWordOrPhraseFab,
                    i.bottom,
                    Gravity.END | Gravity.BOTTOM);

            mBinding.addWordOrPhraseFab.setLayoutParams(newFabParams);
            mBinding.wordsAndPhrasesListRecyclerView.setPadding(0, 0, 0, i.bottom);
            mBinding.wordsAndPhrasesListRecyclerView.setClipToPadding(false);

            mMostRecentBottomInset = i.bottom;

            return WindowInsetsCompat.CONSUMED;
        });
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

    private void setObservers() {
        mViewModel.getAll().observe(this, mItemsObserver);
        mViewModel.getAllAttachedImagesListItemIds().observe(this, integers -> {
            mViewModel.setAttachedImagesListItemIdsCache(integers);
            updateListIfEnoughData();
        });
    }

    private void replaceItemsObserverAccordingToSearchQuery(String query) {
        mViewModel.getAll().removeObservers(this);

        if (mLastSearchLiveData != null) {
            mLastSearchLiveData.removeObservers(this);
        }

        mLastSearchLiveData = mViewModel.getItemsSearch(query);
        mLastSearchLiveData.observe(this, mItemsObserver);
    }

    private void updateListIfEnoughData() {
        List<LanguagesListItem> itemsCache = mViewModel.getAllItemsCache();
        List<Integer> attachedImagesListItemIdsCache = mViewModel.getAttachedImagesListItemIdsCache();

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
                        mBinding.addWordOrPhraseFab.show();
                    }
                });
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

    @SuppressLint("WrongConstant")
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
                UiHelper.showSnackbar(this, mBinding.getRoot(),
                        R.string.list_item_deleted_snackbar, mMostRecentBottomInset);
            };
            deletionHandler.postDelayed(deletionRunnable,
                    Constants.UNDO_DELETE_TIMEOUT_MILLIS + 300);

            UiHelper.makeSnackbar(this, mBinding.getRoot(),
                            R.string.list_item_scheduled_for_deletion_snackbar,
                            mMostRecentBottomInset,
                            Constants.UNDO_DELETE_TIMEOUT_MILLIS)
                    .setAction(R.string.cancel, v -> deletionHandler.removeCallbacks(deletionRunnable))
                    .show();
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
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
