package com.na21k.schedulenotes.ui.lists.languages;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.databinding.ActivityLanguagesListBinding;

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
        observeItems(adapter);
    }

    private void observeItems(LanguagesListAdapter adapter) {
        mViewModel.getAll().observe(this, adapter::setData);
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

    }

    @Override
    public void onShoppingItemUpdateRequested(LanguagesListItem item) {

    }

    @Override
    public void onShoppingItemDeletionRequested(LanguagesListItem item) {
        mViewModel.delete(item);
        Snackbar.make(mBinding.getRoot(), R.string.list_item_deleted_snackbar, 7000)
                .setAction(R.string.undo, v -> mViewModel.addNew(item)).show();
    }
}
