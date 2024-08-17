package com.na21k.schedulenotes.repositories.lists;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItemDao;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanListRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

public class MusicListRepository extends MutableRepository<MusicListItem, Void>
        implements CanListRepository<MusicListItem>, CanSearchRepository<MusicListItem>,
        CanClearRepository {

    private final MusicListItemDao mMusicListItemDao = db.musicListItemDao();
    private final LiveData<List<MusicListItem>> mAllItems = mMusicListItemDao.getAll();

    public MusicListRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<MusicListItem> getById(int id) {
        return mMusicListItemDao.getById(id);
    }

    @Override
    public LiveData<List<MusicListItem>> getAll() {
        return mAllItems;
    }

    @Override
    public List<MusicListItem> getAllBlocking() {
        return mMusicListItemDao.getAllBlocking();
    }

    @Override
    public LiveData<List<MusicListItem>> getSearch(String query) {
        return mMusicListItemDao.search(query);
    }

    @Override
    public Task<Void> add(MusicListItem item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mMusicListItemDao.insert(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> add(List<MusicListItem> items) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mMusicListItemDao.insert(items);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public void addBlocking(List<MusicListItem> items) {
        mMusicListItemDao.insert(items);
    }

    @Override
    public Task<Void> update(MusicListItem item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mMusicListItemDao.update(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> delete(MusicListItem item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mMusicListItemDao.delete(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> deleteAll() {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mMusicListItemDao.deleteAll();
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public void deleteAllBlocking() {
        mMusicListItemDao.deleteAll();
    }
}
