package com.na21k.schedulenotes.ui.lists.languages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.models.LanguagesListItemModel;
import com.na21k.schedulenotes.databinding.ActivityLanguagesListBinding;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.WordOrPhraseDetailsActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LanguagesListActivity extends AppCompatActivity
        implements LanguagesListAdapter.OnLanguagesItemActionRequestedListener {

    private LanguagesListViewModel mViewModel;
    private ActivityLanguagesListBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(LanguagesListViewModel.class);
        mBinding = ActivityLanguagesListBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setUpList();
        setListeners();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setUpList() {
        RecyclerView recyclerView = mBinding.wordsAndPhrasesListRecyclerView;
        LanguagesListAdapter adapter = new LanguagesListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setObservers(adapter);
    }

    private void setObservers(LanguagesListAdapter adapter) {
        mViewModel.getAll().observe(this, items -> {
            mViewModel.setAllItemsCache(items);
            updateListIfEnoughData(adapter);
        });
        mViewModel.getAllAttachedImagesListItemIds().observe(this, integers -> {
            mViewModel.setAttachedImagesListItemIdsCache(integers);
            updateListIfEnoughData(adapter);
        });
    }

    private void updateListIfEnoughData(LanguagesListAdapter adapter) {
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

        models.sort(Comparator.comparing(LanguagesListItemModel::getText));
        adapter.setData(models);
    }

    private void setListeners() {
        mBinding.addWordOrPhraseFab.setOnClickListener(v -> addItem());

        mBinding.wordsAndPhrasesListRecyclerView.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
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
                Snackbar.make(mBinding.getRoot(), R.string.list_item_deleted_snackbar,
                        3000).show();
            };
            deletionHandler.postDelayed(deletionRunnable,
                    Constants.UNDO_DELETE_TIMEOUT_MILLIS + 300);

            Snackbar.make(mBinding.getRoot(), R.string.list_item_scheduled_for_deletion_snackbar,
                    Constants.UNDO_DELETE_TIMEOUT_MILLIS)
                    .setAction(R.string.cancel, v -> deletionHandler.removeCallbacks(deletionRunnable))
                    .show();
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }
}
