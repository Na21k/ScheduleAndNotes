package com.na21k.schedulenotes.ui.lists.userDefinedLists;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.databinding.ActivityUserDefinedListBinding;
import com.na21k.schedulenotes.databinding.UserDefinedListItemInfoAlertViewBinding;
import com.na21k.schedulenotes.helpers.UiHelper;

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
        setListeners();
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
    }

    private void observeItems(UserDefinedListAdapter adapter) {
        mViewModel.getItemsByListId(getListId()).observe(this, userDefinedListItems -> {
            userDefinedListItems.sort(Comparator.comparing(UserDefinedListItem::getText));
            adapter.setItems(userDefinedListItems);
        });
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
        builder.setNeutralButton(R.string.cancel, (dialog, which) -> {
        });
        builder.setNegativeButton(R.string.delete,
                (dialog, which) -> onItemDeletionRequested(userDefinedListItem));

        builder.show();
    }

    @SuppressLint("WrongConstant")
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
