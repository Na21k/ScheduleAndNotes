package com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.databinding.ActivityWordOrPhraseDetailsBinding;

public class WordOrPhraseDetailsActivity extends AppCompatActivity {

    private WordOrPhraseDetailsViewModel mViewModel;
    private ActivityWordOrPhraseDetailsBinding mBinding;
    private LanguagesListItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(WordOrPhraseDetailsViewModel.class);
        mBinding = ActivityWordOrPhraseDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (isEditing()) {
            setTitle(R.string.title_edit_item);

            Bundle bundle = getIntent().getExtras();
            int itemId = bundle.getInt(Constants.LANGUAGES_LIST_ITEM_ID_INTENT_KEY);
            loadItemFromDb(itemId);
        } else {
            setTitle(R.string.title_create_item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.word_or_phrase_details_menu, menu);

        if (!isEditing()) {
            menu.removeItem(R.id.menu_delete);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                saveItem();
                break;
            case R.id.menu_cancel:
                finish();
                break;
            case R.id.menu_delete:
                deleteItem();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadItemFromDb(int itemId) {
        mViewModel.getById(itemId).observe(this, item -> {
            if (item != null) {
                mItem = item;
                mBinding.wordOrPhrase.setText(item.getText());
                mBinding.translation.setText(item.getTranslation());
                mBinding.explanation.setText(item.getExplanation());
                mBinding.usageExample.setText(item.getUsageExampleText());
            }
        });
    }

    private void saveItem() {
        String wordOrPhrase = mBinding.wordOrPhrase.getText().toString();

        if (wordOrPhrase.isEmpty()) {
            Snackbar.make(mBinding.getRoot(), R.string.specify_word_or_phrase_snackbar, 3000)
                    .show();
            return;
        }

        String translation = mBinding.translation.getText().toString();
        String explanation = mBinding.explanation.getText().toString();
        String usageExample = mBinding.usageExample.getText().toString();

        if (isEditing() && mItem != null) {
            mItem.setText(wordOrPhrase);
            mItem.setTranslation(translation);
            mItem.setExplanation(explanation);
            mItem.setUsageExampleText(usageExample);

            mViewModel.update(mItem);
        } else {
            mViewModel.addNew(new LanguagesListItem(0, wordOrPhrase,
                    translation, explanation, usageExample));
        }

        finish();
    }

    private void deleteItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_24);
        builder.setTitle(R.string.list_item_deletion_alert_title);
        builder.setMessage(R.string.list_item_deletion_alert_message);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            mViewModel.delete(mItem);
            finish();
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }

    private boolean isEditing() {
        Bundle bundle = getIntent().getExtras();
        return bundle != null;
    }
}
