package com.na21k.schedulenotes.repositories.lists.languages;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemDao;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanProvideRandomRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

import javax.inject.Inject;

public class LanguagesListRepository extends MutableRepository<LanguagesListItem>
        implements CanSearchRepository<LanguagesListItem>, CanClearRepository,
        CanProvideRandomRepository<LanguagesListItem> {

    private final LanguagesListItemDao mLanguagesListItemDao = db.languagesListItemDao();

    @Inject
    public LanguagesListRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LanguagesListItem getRandomBlocking() {
        return mLanguagesListItemDao.getRandomBlocking();
    }

    public LiveData<List<LanguagesListItem>> getUnarchived() {
        return mLanguagesListItemDao.getUnarchived();
    }

    public LiveData<List<LanguagesListItem>> getArchived() {
        return mLanguagesListItemDao.getArchived();
    }

    public Task<Boolean> isArchiveEmpty() {
        TaskCompletionSource<Boolean> source = new TaskCompletionSource<>();

        new Thread(() -> {
            boolean isEmpty = mLanguagesListItemDao.isArchiveEmptyBlocking();
            source.setResult(isEmpty);
        }).start();

        return source.getTask();
    }

    public Task<Void> setArchived(int itemId, boolean archived) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mLanguagesListItemDao.setArchived(itemId, archived);
            source.setResult(null);
        }).start();

        return source.getTask();
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
