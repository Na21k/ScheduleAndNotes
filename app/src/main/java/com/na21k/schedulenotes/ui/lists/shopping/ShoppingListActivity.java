package com.na21k.schedulenotes.ui.lists.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.ScheduleNotesApplication;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.databinding.ActivityShoppingListBinding;
import com.na21k.schedulenotes.databinding.ShoppingListItemInfoAlertViewBinding;
import com.na21k.schedulenotes.helpers.UiHelper;

import java.util.List;

import javax.inject.Inject;

public class ShoppingListActivity extends AppCompatActivity
        implements ShoppingListAdapter.OnShoppingItemActionRequestedListener {

    private static final int mLandscapeColumnCount = 2;
    private static final int mPortraitColumnCountTablet = 2;
    private static final int mLandscapeColumnCountTablet = 3;
    private final Observer<List<ShoppingListItem>> mItemsObserver = new Observer<List<ShoppingListItem>>() {
        @Override
        public void onChanged(List<ShoppingListItem> goods) {
            mListAdapter.setGoods(goods);
            updateCheckedPrice(goods);
            updateTotalPrice(goods);
        }
    };
    @Inject
    protected ShoppingListViewModel.Factory mViewModelFactory;
    private ShoppingListViewModel mViewModel;
    private ActivityShoppingListBinding mBinding;
    private ShoppingListAdapter mListAdapter;
    private LiveData<List<ShoppingListItem>> mLastSearchLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((ScheduleNotesApplication) getApplicationContext())
                .getAppComponent()
                .inject(this);
        super.onCreate(savedInstanceState);
        ensureBackStackPresent();
        ShortcutManagerCompat.reportShortcutUsed(this, "shopping_list_shortcut");

        mViewModel = new ViewModelProvider(this, mViewModelFactory).get(ShoppingListViewModel.class);
        mBinding = ActivityShoppingListBinding.inflate(getLayoutInflater());
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
        getMenuInflater().inflate(R.menu.shopping_list_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                mBinding.totalPriceArea.setVisibility(View.GONE);
                mBinding.itemAdditionArea.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                mBinding.totalPriceArea.setVisibility(View.VISIBLE);
                mBinding.itemAdditionArea.setVisibility(View.VISIBLE);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_clear) {
            clearList();
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
                mBinding.container, mBinding.itemAdditionArea, null, true);
    }

    private void clearList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_sweep_24);
        builder.setTitle(R.string.clear_shopping_list_alert_title);
        builder.setMessage(R.string.clear_shopping_list_alert_message);

        builder.setPositiveButton(R.string.clear_list_dialog_button, (dialog, which) -> {
            mViewModel.deleteAll();
            Snackbar.make(mBinding.getRoot(),
                            R.string.list_cleared_snackbar,
                            Constants.DEFAULT_SNACKBAR_TIMEOUT_MILLIS)
                    .setAnchorView(mBinding.itemAdditionLinearLayout).show();
        });
        builder.setNeutralButton(R.string.clear_checked_dialog_button, (dialog, which) -> {
            mViewModel.deleteChecked();
            Snackbar.make(mBinding.getRoot(),
                            R.string.list_cleared_of_checked_items_snackbar,
                            Constants.DEFAULT_SNACKBAR_TIMEOUT_MILLIS)
                    .setAnchorView(mBinding.itemAdditionLinearLayout).show();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });

        builder.show();
    }

    /**
     * A hack for when the Activity is launched from the app shortcut.</br>
     * </br>
     * Since there is no way to control the activity-launching intent creation directly
     * and create it with TaskStackBuilder, restart the activity with TaskStackBuilder here,
     * having the back stack constructed.
     */
    private void ensureBackStackPresent() {
        if (isTaskRoot()) {
            Intent intent = new Intent(this, this.getClass());

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(intent);

            taskStackBuilder.startActivities();
            finish();
        }
    }

    private void setUpList() {
        RecyclerView recyclerView = mBinding.shoppingListRecyclerView;
        mListAdapter = new ShoppingListAdapter(this);
        recyclerView.setAdapter(mListAdapter);
        LinearLayoutManager layoutManager = UiHelper.getRecyclerViewLayoutManager(this,
                mLandscapeColumnCountTablet, mPortraitColumnCountTablet, mLandscapeColumnCount);
        recyclerView.setLayoutManager(layoutManager);
        observeItems();
    }

    private void observeItems() {
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
            Snackbar.make(mBinding.getRoot(),
                            R.string.specify_item_text_snackbar,
                            Constants.DEFAULT_SNACKBAR_TIMEOUT_MILLIS)
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
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });
        builder.setNeutralButton(R.string.delete,
                (dialog, which) -> onShoppingItemDeletionRequested(item));

        AlertDialog alertDialog = builder.show();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onShoppingItemSilentUpdateRequested(ShoppingListItem item) {
        mViewModel.update(item);
    }

    @Override
    public void onShoppingItemDeletionRequested(ShoppingListItem item) {
        mViewModel.delete(item);
        Snackbar.make(mBinding.getRoot(), R.string.list_item_deleted_snackbar,
                        Constants.UNDO_DELETE_TIMEOUT_MILLIS)
                .setAnchorView(mBinding.itemAdditionLinearLayout)
                .setAction(R.string.undo, v -> mViewModel.addNew(item)).show();
    }
}
