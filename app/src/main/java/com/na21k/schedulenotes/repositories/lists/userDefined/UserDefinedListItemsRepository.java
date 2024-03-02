package com.na21k.schedulenotes.repositories.lists.userDefined;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItemDao;
import com.na21k.schedulenotes.repositories.CanListRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

public class UserDefinedListItemsRepository extends MutableRepository<UserDefinedListItem, Void>
        implements CanListRepository<UserDefinedListItem> {

    private final UserDefinedListItemDao mUserDefinedListItemDao = db.userDefinedListItemDao();

    public UserDefinedListItemsRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<List<UserDefinedListItem>> getAll() {
        return mUserDefinedListItemDao.getAll();
    }

    public LiveData<List<UserDefinedListItem>> getAllForList(int listId) {
        return mUserDefinedListItemDao.getByListId(listId);
    }

    @Override
    public LiveData<UserDefinedListItem> getById(int id) {
        return mUserDefinedListItemDao.getById(id);
    }

    public LiveData<List<UserDefinedListItem>> getSearch(int listId, String query) {
        return mUserDefinedListItemDao.searchInList(listId, query);
    }

    @Override
    public Task<Void> add(UserDefinedListItem item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mUserDefinedListItemDao.insert(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> add(List<UserDefinedListItem> items) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mUserDefinedListItemDao.insert(items);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> update(UserDefinedListItem item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mUserDefinedListItemDao.update(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> delete(UserDefinedListItem item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mUserDefinedListItemDao.delete(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }
}
