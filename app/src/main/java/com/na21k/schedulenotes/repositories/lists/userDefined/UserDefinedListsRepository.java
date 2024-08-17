package com.na21k.schedulenotes.repositories.lists.userDefined;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListDao;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanListRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

public class UserDefinedListsRepository extends MutableRepository<UserDefinedList, Void>
        implements CanListRepository<UserDefinedList>, CanSearchRepository<UserDefinedList>,
        CanClearRepository {

    private final UserDefinedListDao mUserDefinedListDao = db.userDefinedListDao();
    private final LiveData<List<UserDefinedList>> mAllLists = mUserDefinedListDao.getAll();

    public UserDefinedListsRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<List<UserDefinedList>> getAll() {
        return mAllLists;
    }

    @Override
    public List<UserDefinedList> getAllBlocking() {
        return mUserDefinedListDao.getAllBlocking();
    }

    @Override
    public LiveData<UserDefinedList> getById(int id) {
        return mUserDefinedListDao.getById(id);
    }

    @Override
    public LiveData<List<UserDefinedList>> getSearch(String query) {
        return mUserDefinedListDao.search(query);
    }

    @Override
    public Task<Void> add(UserDefinedList item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mUserDefinedListDao.insert(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> add(List<UserDefinedList> items) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mUserDefinedListDao.insert(items);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public void addBlocking(List<UserDefinedList> items) {
        mUserDefinedListDao.insert(items);
    }

    @Override
    public Task<Void> update(UserDefinedList item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            try {
                mUserDefinedListDao.update(item);
                source.setResult(null);
            } catch (Exception e) {
                source.setException(e);
            }
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> delete(UserDefinedList item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mUserDefinedListDao.delete(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> deleteAll() {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mUserDefinedListDao.deleteAll();
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public void deleteAllBlocking() {
        mUserDefinedListDao.deleteAll();
    }
}
