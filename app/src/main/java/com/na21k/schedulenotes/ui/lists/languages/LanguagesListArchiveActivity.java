package com.na21k.schedulenotes.ui.lists.languages;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.data.models.LanguagesListItemModel;
import com.na21k.schedulenotes.databinding.ActivityLanguagesListArchiveBinding;
import com.na21k.schedulenotes.helpers.UiHelper;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.WordOrPhraseDetailsActivity;

public class LanguagesListArchiveActivity extends AppCompatActivity
        implements LanguagesListAdapter.OnLanguagesItemActionRequestedListener {

    private static final int mLandscapeColumnCount = 2;
    private static final int mPortraitColumnCountTablet = 2;
    private static final int mLandscapeColumnCountTablet = 3;
    private LanguagesListArchiveViewModel mViewModel;
    private ActivityLanguagesListArchiveBinding mBinding;
    private LanguagesListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(LanguagesListArchiveViewModel.class);
        mBinding = ActivityLanguagesListArchiveBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar.appBar);

        makeNavBarLookNice();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setUpList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void makeNavBarLookNice() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.getRoot(), (v, insets) -> {
            Insets i = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());

            mBinding.getRoot().setPadding(i.left, i.top, i.right, 0);
            mBinding.wordsAndPhrasesListRecyclerView.setPadding(0, 0, 0, i.bottom);
            mBinding.wordsAndPhrasesListRecyclerView.setClipToPadding(false);

            //mMostRecentBottomInset = i.bottom;

            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setUpList() {
        RecyclerView recyclerView = mBinding.wordsAndPhrasesListRecyclerView;
        mListAdapter = new LanguagesListAdapter(this);
        recyclerView.setAdapter(mListAdapter);
        LinearLayoutManager layoutManager = UiHelper.getRecyclerViewLayoutManager(this,
                mLandscapeColumnCountTablet, mPortraitColumnCountTablet, mLandscapeColumnCount);
        recyclerView.setLayoutManager(layoutManager);
        setObservers();
    }

    private void setObservers() {

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

    @Override
    public void onItemDeletionRequested(LanguagesListItemModel item) {

    }
}
