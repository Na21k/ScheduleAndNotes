package com.na21k.schedulenotes.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Categories.CategoryDao;

import java.util.List;

public class CategoriesRepository extends MutableRepository<Category, Void>
        implements CanListRepository<Category>, CanSearchRepository<Category>, CanClearRepository {

    private final CategoryDao mCategoryDao = db.categoryDao();
    private final LiveData<List<Category>> mAllItems = mCategoryDao.getAll();

    public CategoriesRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<Category> getById(int id) {
        return mCategoryDao.getById(id);
    }

    @Override
    public LiveData<List<Category>> getAll() {
        return mAllItems;
    }

    @Override
    public List<Category> getAllBlocking() {
        return mCategoryDao.getAllBlocking();
    }

    @Override
    public LiveData<List<Category>> getSearch(String query) {
        return mCategoryDao.search(query);
    }

    @Override
    public Task<Void> add(Category item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mCategoryDao.insert(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> add(List<Category> items) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mCategoryDao.insert(items);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public void addBlocking(List<Category> items) {
        mCategoryDao.insert(items);
    }

    @Override
    public Task<Void> update(Category item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mCategoryDao.update(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> delete(Category item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mCategoryDao.delete(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    public Task<Void> delete(int itemId) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mCategoryDao.delete(itemId);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public void deleteAllBlocking() {
        mCategoryDao.deleteAll();
    }
}
