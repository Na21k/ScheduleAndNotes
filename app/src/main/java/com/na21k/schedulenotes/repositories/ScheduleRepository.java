package com.na21k.schedulenotes.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventDao;

import java.util.Date;
import java.util.List;

public class ScheduleRepository extends MutableRepository<Event, Long>
        implements CanListRepository<Event>, CanSearchRepository<Event>, CanClearRepository {

    private final EventDao mEventDao = db.eventDao();
    private final LiveData<List<Event>> mAllItems = mEventDao.getAll();

    public ScheduleRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<Event> getById(int id) {
        return mEventDao.getById(id);
    }

    public Event getByIdBlocking(int id) {
        return mEventDao.getByIdBlocking(id);
    }

    public LiveData<List<Event>> getByDate(Date hasStartedBefore, Date hasNotEndedBy) {
        return mEventDao.getByDate(hasStartedBefore, hasNotEndedBy);
    }

    @Override
    public LiveData<List<Event>> getAll() {
        return mAllItems;
    }

    @Override
    public List<Event> getAllBlocking() {
        return mEventDao.getAllBlocking();
    }

    @Override
    public LiveData<List<Event>> getSearch(String query) {
        return mEventDao.search(query);
    }

    @Override
    public Task<Long> add(Event item) {
        TaskCompletionSource<Long> source = new TaskCompletionSource<>();

        new Thread(() -> {
            long id = mEventDao.insert(item);
            source.setResult(id);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> add(List<Event> items) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mEventDao.insert(items);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public void addBlocking(List<Event> items) {
        mEventDao.insert(items);
    }

    @Override
    public Task<Void> update(Event item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mEventDao.update(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> delete(Event item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mEventDao.delete(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    public Task<Void> delete(int itemId) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mEventDao.delete(itemId);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    public Task<Void> deleteOlderThan(Date date) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mEventDao.deleteOlderThan(date);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> deleteAll() {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mEventDao.deleteAll();
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public void deleteAllBlocking() {
        mEventDao.deleteAll();
    }
}
