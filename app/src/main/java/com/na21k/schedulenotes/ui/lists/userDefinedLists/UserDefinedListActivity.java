package com.na21k.schedulenotes.ui.lists.userDefinedLists;

import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.databinding.ActivityUserDefinedListBinding;
import com.na21k.schedulenotes.databinding.UserDefinedListItemInfoAlertViewBinding;
import com.na21k.schedulenotes.helpers.UiHelper;

import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

public class UserDefinedListActivity extends AppCompatActivity
        implements UserDefinedListAdapter.OnItemActionRequestedListener {

    private static final int mLandscapeColumnCount = 2;
    private static final int mPortraitColumnCountTablet = 2;
    private static final int mLandscapeColumnCountTablet = 3;
    private final Observer<List<UserDefinedListItem>> mItemsObserver = new Observer<List<UserDefinedListItem>>() {
        @Override
        public void onChanged(List<UserDefinedListItem> userDefinedListItems) {
            userDefinedListItems.sort(Comparator.comparing(UserDefinedListItem::getText));
            mListAdapter.setItems(userDefinedListItems);
        }
    };
    @Inject
    protected UserDefinedListViewModel.Factory mViewModelFactory;
    private UserDefinedListViewModel mViewModel;
    private ActivityUserDefinedListBinding mBinding;
    private UserDefinedListAdapter mListAdapter;
    private LiveData<List<UserDefinedListItem>> mLastLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((ScheduleNotesApplication) getApplicationContext())
                .getAppComponent()
                .inject(this);
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, mViewModelFactory).get(UserDefinedListViewModel.class);
        mViewModel.configure(getListId());
        mBinding = ActivityUserDefinedListBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar.appBar);

        handleWindowInsets();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        updateTitle();
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
                mBinding.itemAdditionLinearLayout.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                mBinding.itemAdditionLinearLayout.setVisibility(View.VISIBLE);
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
                mBinding.container, mBinding.itemAdditionLinearLayout, null, true);
    }

    private void setUpList() {
        RecyclerView recyclerView = mBinding.includedList.simpleList;
        mListAdapter = new UserDefinedListAdapter(this);
        recyclerView.setAdapter(mListAdapter);
        LinearLayoutManager layoutManager = UiHelper.getRecyclerViewLayoutManager(this,
                mLandscapeColumnCountTablet, mPortraitColumnCountTablet, mLandscapeColumnCount);
        recyclerView.setLayoutManager(layoutManager);
        observeItems();
    }

    private void observeItems() {
        mLastLiveData = mViewModel.getItems();
        mLastLiveData.observe(this, mItemsObserver);
    }

    private void replaceItemsObserverAccordingToSearchQuery(String query) {
        mLastLiveData.removeObservers(this);

        mLastLiveData = mViewModel.getItemsSearch(query);
        mLastLiveData.observe(this, mItemsObserver);
    }

    private void setListeners() {
        mBinding.addItemBtn.setOnClickListener(v -> addItem());
    }

    private void addItem() {
        Editable addEditTextEditable = mBinding.addItemEditText.getText();

        if (addEditTextEditable != null && !addEditTextEditable.toString().isEmpty()) {
            String newItemText = addEditTextEditable.toString();
            UserDefinedListItem newItem = new UserDefinedListItem(0, newItemText, getListId());
            mViewModel.addNew(newItem);

            mBinding.addItemEditText.setText("");
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_edit_24);
        builder.setTitle(R.string.list_item_editing_alert_title);

        UserDefinedListItemInfoAlertViewBinding viewBinding =
                UserDefinedListItemInfoAlertViewBinding
                        .inflate(getLayoutInflater(), mBinding.getRoot(), false);
        viewBinding.input.setText(userDefinedListItem.getText());
        viewBinding.input.requestFocus();
        builder.setView(viewBinding.getRoot());

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            Editable itemTextEditable = viewBinding.input.getText();

            if (itemTextEditable != null && !itemTextEditable.toString().isEmpty()) {
                String itemText = itemTextEditable.toString();
                userDefinedListItem.setText(itemText);
                mViewModel.update(userDefinedListItem);
            } else {
                onItemUpdateRequested(userDefinedListItem);
                UiHelper.showErrorDialog(this,
                        R.string.list_item_editing_empty_input_alert_message);
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });
        builder.setNeutralButton(R.string.delete,
                (dialog, which) -> onItemDeletionRequested(userDefinedListItem));

        AlertDialog alertDialog = builder.show();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onItemDeletionRequested(UserDefinedListItem userDefinedListItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_24);
        builder.setTitle(R.string.list_item_deletion_alert_title);
        builder.setMessage(R.string.list_item_deletion_alert_message);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            mViewModel.delete(userDefinedListItem);
            Snackbar.make(mBinding.getRoot(), R.string.list_item_deleted_snackbar,
                            Constants.UNDO_DELETE_TIMEOUT_MILLIS)
                    .setAnchorView(mBinding.itemAdditionLinearLayout)
                    .setAction(R.string.undo, v -> mViewModel.addNew(userDefinedListItem)).show();
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }
}
