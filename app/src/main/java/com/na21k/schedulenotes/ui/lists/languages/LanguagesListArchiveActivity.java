package com.na21k.schedulenotes.ui.lists.languages;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.models.LanguagesListItemModel;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.WordOrPhraseDetailsActivity;

public class LanguagesListArchiveActivity extends LanguagesListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableAddingItems();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.removeItem(R.id.menu_search);
        menu.removeItem(R.id.menu_archived);

        return true;
    }

    @Override
    protected void setObservers() {
        mViewModel.getArchived().observe(this, mItemsObserver);
        mViewModel.getAllAttachedImagesListItemIds().observe(this, integers -> {
            mViewModel.setAttachedImagesListItemIdsCache(integers);
            updateListIfEnoughData();
        });
    }

    @Override
    public void onItemUpdateRequested(LanguagesListItemModel item) {
        Intent intent = new Intent(this, WordOrPhraseDetailsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LANGUAGES_LIST_ITEM_ID_INTENT_KEY, item.getId());
        bundle.putBoolean(Constants.IS_OPEN_FROM_ARCHIVE_INTENT_KEY, true);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
