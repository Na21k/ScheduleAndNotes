package com.na21k.schedulenotes.repositories.lists;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItemDao;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanListRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

public class MoviesListRepository extends MutableRepository<MoviesListItem, Void>
        implements CanSearchRepository<MoviesListItem>, CanListRepository<MoviesListItem>,
        CanClearRepository {

    private final MoviesListItemDao mMoviesListItemDao = db.moviesListItemDao();
    private final LiveData<List<MoviesListItem>> mAllItems = mMoviesListItemDao.getAll();

    public MoviesListRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<MoviesListItem> getById(int id) {
        return mMoviesListItemDao.getById(id);
    }

    @Override
    public LiveData<List<MoviesListItem>> getAll() {
        return mAllItems;
    }

    @Override
    public LiveData<List<MoviesListItem>> getSearch(String query) {
        return mMoviesListItemDao.search(query);
    }

    @Override
    public Task<Void> add(MoviesListItem item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mMoviesListItemDao.insert(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> add(List<MoviesListItem> items) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mMoviesListItemDao.insert(items);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> update(MoviesListItem item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mMoviesListItemDao.update(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> delete(MoviesListItem item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mMoviesListItemDao.delete(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> deleteAll() {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mMoviesListItemDao.deleteAll();
            source.setResult(null);
        }).start();

        return source.getTask();
    }
}
