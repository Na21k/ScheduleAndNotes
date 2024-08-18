package com.na21k.schedulenotes.repositories.lists.languages;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemDao;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanListRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

public class LanguagesListRepository extends MutableRepository<LanguagesListItem, Long>
        implements CanSearchRepository<LanguagesListItem>, CanListRepository<LanguagesListItem>,
        CanClearRepository {

    private final LanguagesListItemDao mLanguagesListItemDao = db.languagesListItemDao();
    private final LiveData<List<LanguagesListItem>> mAllItems = mLanguagesListItemDao.getAll();

    public LanguagesListRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<LanguagesListItem> getById(int id) {
        return mLanguagesListItemDao.getById(id);
    }

    @Override
    public LiveData<List<LanguagesListItem>> getAll() {
        return mAllItems;
    }

    @Override
    public List<LanguagesListItem> getAllBlocking() {
        return mLanguagesListItemDao.getAllBlocking();
    }

    @Override
    public LiveData<List<LanguagesListItem>> getSearch(String query) {
        return mLanguagesListItemDao.search(query);
    }

    @Override
    public Task<Long> add(LanguagesListItem item) {
        TaskCompletionSource<Long> source = new TaskCompletionSource<>();

        new Thread(() -> {
            long itemId = mLanguagesListItemDao.insert(item);
            source.setResult(itemId);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> add(List<LanguagesListItem> items) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mLanguagesListItemDao.insert(items);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public void addBlocking(List<LanguagesListItem> items) {
        mLanguagesListItemDao.insert(items);
    }

    @Override
    public Task<Void> update(LanguagesListItem item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mLanguagesListItemDao.update(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> delete(LanguagesListItem item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mLanguagesListItemDao.delete(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public void deleteAllBlocking() {
        mLanguagesListItemDao.deleteAll();
    }
}
