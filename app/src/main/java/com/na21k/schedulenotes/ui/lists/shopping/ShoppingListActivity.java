package com.na21k.schedulenotes.ui.lists.shopping;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.databinding.ActivityShoppingListBinding;

public class ShoppingListActivity extends AppCompatActivity
        implements ShoppingListAdapter.OnShoppingItemActionRequestedListener{

    private ShoppingListViewModel mViewModel;
    private ActivityShoppingListBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(ShoppingListViewModel.class);
        mBinding = ActivityShoppingListBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onShoppingItemUpdateRequested(ShoppingListItem item) {

    }

    @Override
    public void onShoppingItemDeletionRequested(ShoppingListItem item) {

    }
}
