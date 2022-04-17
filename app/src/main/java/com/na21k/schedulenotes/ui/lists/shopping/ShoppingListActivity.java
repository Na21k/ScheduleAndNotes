package com.na21k.schedulenotes.ui.lists.shopping;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.databinding.ActivityShoppingListBinding;
import com.na21k.schedulenotes.databinding.ShoppingListItemInfoAlertViewBinding;
import com.na21k.schedulenotes.helpers.UiHelper;

public class ShoppingListActivity extends AppCompatActivity
        implements ShoppingListAdapter.OnShoppingItemActionRequestedListener {

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

        setUpList();
        setListeners();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shopping_list_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_clear) {
            clearList();
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_sweep_24);
        builder.setTitle(R.string.clear_shopping_list_alert_title);
        builder.setMessage(R.string.clear_shopping_list_alert_message);

        builder.setPositiveButton(R.string.clear_list_dialog_button, (dialog, which) -> {
            mViewModel.deleteAll();
            Snackbar.make(mBinding.getRoot(), R.string.list_cleared_snackbar, 3000)
                    .setAnchorView(mBinding.itemAdditionLinearLayout).show();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });

        builder.show();
    }

    private void setUpList() {
        RecyclerView recyclerView = mBinding.shoppingListRecyclerView;
        ShoppingListAdapter adapter = new ShoppingListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        observeItems(adapter);
    }

    private void observeItems(ShoppingListAdapter adapter) {
        mViewModel.getAll().observe(this, adapter::setGoods);
    }

    private void setListeners() {
        mBinding.addItemPriceEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                addItem();
                return true;
            }

            return false;
        });
    }

    private void addItem() {
        String newItemText = mBinding.addItemEditText.getText().toString();
        String newItemPriceStr = mBinding.addItemPriceEditText.getText().toString();

        if (!newItemText.isEmpty()) {
            ShoppingListItem newItem = new ShoppingListItem(0, newItemText, 0f);

            try {
                float newItemPrice = Float.parseFloat(newItemPriceStr);
                newItem.setPrice(newItemPrice);
            } catch (NumberFormatException ignored) {
            } finally {
                mViewModel.addNew(newItem);
            }

            mBinding.addItemEditText.setText("");
            mBinding.addItemPriceEditText.setText("");
        } else {
            Snackbar.make(mBinding.getRoot(), R.string.specify_item_text_snackbar, 3000)
                    .setAnchorView(mBinding.itemAdditionLinearLayout).show();
        }

        mBinding.addItemEditText.requestFocus();
    }

    @Override
    public void onShoppingItemUpdateRequested(ShoppingListItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_edit_24);
        builder.setTitle(R.string.list_item_editing_alert_title);

        ShoppingListItemInfoAlertViewBinding viewBinding = ShoppingListItemInfoAlertViewBinding
                .inflate(getLayoutInflater(), mBinding.getRoot(), false);
        viewBinding.itemTextEditText.setText(item.getText());
        viewBinding.itemPriceEditText.setText(String.valueOf(item.getPrice()));
        builder.setView(viewBinding.getRoot());

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String itemText = viewBinding.itemTextEditText.getText().toString();
            String itemPriceStr = viewBinding.itemPriceEditText.getText().toString();

            if (!itemText.isEmpty()) {
                item.setText(itemText);

                try {
                    float newItemPrice = Float.parseFloat(itemPriceStr);
                    item.setPrice(newItemPrice);
                } catch (NumberFormatException ex) {
                    item.setPrice(0f);
                }

                mViewModel.update(item);
            } else {
                onShoppingItemUpdateRequested(item);
                UiHelper.showErrorDialog(this,
                        R.string.list_item_editing_empty_input_alert_message);
            }
        });
        builder.setNeutralButton(R.string.cancel, (dialog, which) -> {
        });
        builder.setNegativeButton(R.string.delete,
                (dialog, which) -> onShoppingItemDeletionRequested(item));

        builder.show();
    }

    @Override
    public void onShoppingItemDeletionRequested(ShoppingListItem item) {
        mViewModel.delete(item);
        Snackbar.make(mBinding.getRoot(), R.string.list_item_deleted_snackbar, 7000)
                .setAnchorView(mBinding.itemAdditionLinearLayout)
                .setAction(R.string.undo, v -> mViewModel.addNew(item)).show();
    }
}
