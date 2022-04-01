package com.na21k.schedulenotes.ui.lists.userDefinedLists;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.databinding.ActivityUserDefinedListBinding;

import java.util.Comparator;

public class UserDefinedListActivity extends AppCompatActivity
        implements UserDefinedListAdapter.OnItemActionRequestedListener {

    private UserDefinedListViewModel mViewModel;
    private ActivityUserDefinedListBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(UserDefinedListViewModel.class);
        mBinding = ActivityUserDefinedListBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        updateTitle();
        setUpList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setUpList() {
        RecyclerView recyclerView = mBinding.includedList.simpleList;
        UserDefinedListAdapter adapter = new UserDefinedListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        observeItems(adapter);
        setListeners();
    }

    private void observeItems(UserDefinedListAdapter adapter) {
        mViewModel.getItemsByListId(getListId()).observe(this, userDefinedListItems -> {
            userDefinedListItems.sort(Comparator.comparing(UserDefinedListItem::getText));
            adapter.setItems(userDefinedListItems);
        });
    }

    private void setListeners() {
        mBinding.addItemFab.setOnClickListener(v -> newItem());

        mBinding.includedList.simpleList.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY <= oldScrollY) {
                        mBinding.addItemFab.extend();
                    } else {
                        mBinding.addItemFab.shrink();
                    }

                    if (!v.canScrollVertically(1) && v.canScrollVertically(-1)) {
                        mBinding.addItemFab.hide();
                    } else {
                        mBinding.addItemFab.show();
                    }
                });
    }

    private void newItem() {

    }

    private int getListId() {
        Bundle bundle = getIntent().getExtras();
        return bundle.getInt(Constants.LIST_ID_INTENT_KEY);
    }

    private void updateTitle() {
        Bundle bundle = getIntent().getExtras();
        String listTitle = bundle.getString(Constants.LIST_TITLE_INTENT_KEY);
        setTitle(listTitle);
    }

    @Override
    public void onItemUpdateRequested(UserDefinedListItem userDefinedListItem) {

    }

    @Override
    public void onItemDeletionRequested(UserDefinedListItem userDefinedListItem) {

    }
}
