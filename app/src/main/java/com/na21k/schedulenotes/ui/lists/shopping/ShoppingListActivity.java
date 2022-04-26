package com.na21k.schedulenotes.ui.lists.shopping;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.databinding.ActivityShoppingListBinding;
import com.na21k.schedulenotes.databinding.ShoppingListItemInfoAlertViewBinding;
import com.na21k.schedulenotes.helpers.UiHelper;

import java.util.List;

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
        mViewModel.getAll().observe(this, goods -> {
            adapter.setGoods(goods);
            updateCheckedPrice(goods);
            updateTotalPrice(goods);
        });
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
        String newItemCountStr = mBinding.addItemCountEditText.getText().toString();
        String newItemPriceStr = mBinding.addItemPriceEditText.getText().toString();

        if (!newItemText.isEmpty()) {
            ShoppingListItem newItem = new ShoppingListItem(0, newItemText, 0f, 1);

            try {
                int newItemCount = Integer.parseInt(newItemCountStr);
                newItem.setCount(newItemCount > 0 ? newItemCount : 1);
            } catch (NumberFormatException ignored) {
            }

            try {
                float newItemPrice = Float.parseFloat(newItemPriceStr);
                newItem.setPrice(newItemPrice);
            } catch (NumberFormatException ignored) {
            }

            mViewModel.addNew(newItem);

            mBinding.addItemEditText.setText("");
            mBinding.addItemCountEditText.setText("1");
            mBinding.addItemPriceEditText.setText("");
        } else {
            Snackbar.make(mBinding.getRoot(), R.string.specify_item_text_snackbar, 3000)
                    .setAnchorView(mBinding.itemAdditionLinearLayout).show();
        }

        mBinding.addItemEditText.requestFocus();
    }

    private void updateCheckedPrice(List<ShoppingListItem> items) {
        float checkedPrice = 0;

        for (ShoppingListItem item : items) {
            if (item.isChecked()) {
                checkedPrice += item.getPrice() * item.getCount();
            }
        }

        mBinding.checkedPriceTextView.setText(String.valueOf(checkedPrice));

        if (checkedPrice == 0f) {
            mBinding.checkedPriceTextView.setVisibility(View.INVISIBLE);
            mBinding.slash.setVisibility(View.INVISIBLE);
        } else {
            mBinding.checkedPriceTextView.setVisibility(View.VISIBLE);
            mBinding.slash.setVisibility(View.VISIBLE);
        }
    }

    private void updateTotalPrice(List<ShoppingListItem> items) {
        float totalPrice = 0;

        for (ShoppingListItem item : items) {
            totalPrice += item.getPrice() * item.getCount();
        }

        mBinding.totalPriceTextView.setText(String.valueOf(totalPrice));

        boolean zeroPricesPresent = items.stream().anyMatch(item -> item.getPrice() == 0f);
        int textColor;

        if (zeroPricesPresent) {
            textColor = ContextCompat.getColor(this, R.color.warning_text);
        } else {
            textColor = ContextCompat.getColor(this, R.color.price_text_color);
        }

        mBinding.totalPriceTextView.setTextColor(textColor);
    }

    @Override
    public void onShoppingItemUpdateRequested(ShoppingListItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_edit_24);
        builder.setTitle(R.string.list_item_editing_alert_title);

        ShoppingListItemInfoAlertViewBinding viewBinding = ShoppingListItemInfoAlertViewBinding
                .inflate(getLayoutInflater(), mBinding.getRoot(), false);
        viewBinding.itemTextEditText.setText(item.getText());
        viewBinding.itemCountEditText.setText(String.valueOf(item.getCount()));
        viewBinding.itemPriceEditText.setText(String.valueOf(item.getPrice()));
        viewBinding.itemTextEditText.requestFocus();
        builder.setView(viewBinding.getRoot());

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String itemText = viewBinding.itemTextEditText.getText().toString();
            String itemCountStr = viewBinding.itemCountEditText.getText().toString();
            String itemPriceStr = viewBinding.itemPriceEditText.getText().toString();

            if (!itemText.isEmpty()) {
                item.setText(itemText);

                try {
                    int newItemCount = Integer.parseInt(itemCountStr);
                    item.setCount(newItemCount > 0 ? newItemCount : 1);
                } catch (NumberFormatException ex) {
                    item.setCount(1);
                }

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
    public void onShoppingItemSilentUpdateRequested(ShoppingListItem item) {
        mViewModel.update(item);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onShoppingItemDeletionRequested(ShoppingListItem item) {
        mViewModel.delete(item);
        Snackbar.make(mBinding.getRoot(), R.string.list_item_deleted_snackbar,
                Constants.UNDO_DELETE_TIMEOUT_MILLIS)
                .setAnchorView(mBinding.itemAdditionLinearLayout)
                .setAction(R.string.undo, v -> mViewModel.addNew(item)).show();
    }
}
