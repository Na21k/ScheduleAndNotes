package com.na21k.schedulenotes.repositories.lists;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItemDao;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanListRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

public class ShoppingListRepository extends MutableRepository<ShoppingListItem, Void>
        implements CanListRepository<ShoppingListItem>, CanSearchRepository<ShoppingListItem>,
        CanClearRepository {

    private final ShoppingListItemDao mShoppingListItemDao = db.shoppingListItemDao();
    private final LiveData<List<ShoppingListItem>> mAllItems = mShoppingListItemDao.getAll();

    public ShoppingListRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<ShoppingListItem> getById(int id) {
        return mShoppingListItemDao.getById(id);
    }

    @Override
    public LiveData<List<ShoppingListItem>> getAll() {
        return mAllItems;
    }

    @Override
    public List<ShoppingListItem> getAllBlocking() {
        return mShoppingListItemDao.getAllBlocking();
    }

    @Override
    public LiveData<List<ShoppingListItem>> getSearch(String query) {
        return mShoppingListItemDao.search(query);
    }

    @Override
    public Task<Void> add(ShoppingListItem item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mShoppingListItemDao.insert(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> add(List<ShoppingListItem> items) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mShoppingListItemDao.insert(items);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public void addBlocking(List<ShoppingListItem> items) {
        mShoppingListItemDao.insert(items);
    }

    @Override
    public Task<Void> update(ShoppingListItem item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mShoppingListItemDao.update(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> delete(ShoppingListItem item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mShoppingListItemDao.delete(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    public void deleteChecked() {
        new Thread(mShoppingListItemDao::deleteChecked).start();
    }

    @Override
    public Task<Void> deleteAll() {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mShoppingListItemDao.deleteAll();
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public void deleteAllBlocking() {
        mShoppingListItemDao.deleteAll();
    }
}
