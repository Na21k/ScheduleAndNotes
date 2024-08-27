package com.na21k.schedulenotes.repositories.lists.languages;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemDao;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

public class LanguagesListRepository extends MutableRepository<LanguagesListItem>
        implements CanSearchRepository<LanguagesListItem>, CanClearRepository {

    private final LanguagesListItemDao mLanguagesListItemDao = db.languagesListItemDao();

    public LanguagesListRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<List<LanguagesListItem>> getSearch(String query) {
        return mLanguagesListItemDao.search(query);
    }

    @Override
    public void clearBlocking() {
        mLanguagesListItemDao.deleteAll();
    }

    @Override
    protected BaseDao<LanguagesListItem> getDao() {
        return mLanguagesListItemDao;
    }
}
