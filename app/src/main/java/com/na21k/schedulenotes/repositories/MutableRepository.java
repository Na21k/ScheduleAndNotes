package com.na21k.schedulenotes.repositories;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.na21k.schedulenotes.data.database.Identifiable;

import java.util.List;

public abstract class MutableRepository<T extends Identifiable> extends Repository<T> {

    protected MutableRepository(@NonNull Context context) {
        super(context);
    }

    public Task<Long> add(T item) {
        TaskCompletionSource<Long> source = new TaskCompletionSource<>();

        new Thread(() -> {
            long id = addBlocking(item);
            source.setResult(id);
        }).start();

        return source.getTask();
    }

    public Task<Void> add(List<T> items) {
        return runSimpleAsync(() -> addBlocking(items));
    }

    public long addBlocking(T item) {
        return getDao().insert(item);
    }

    public void addBlocking(List<T> items) {
        getDao().insert(items);
    }

    public Task<Void> update(T item) {
        return runSimpleAsync(() -> getDao().update(item));
    }

    public Task<Void> delete(T item) {
        return runSimpleAsync(() -> deleteBlocking(item));
    }

    public Task<Void> delete(int itemId) {
        return runSimpleAsync(() -> getDao().delete(itemId));
    }

    public void deleteBlocking(T item) {
        getDao().delete(item);
    }

    protected Task<Void> runSimpleAsync(Runnable r) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            r.run();
            source.setResult(null);
        }).start();

        return source.getTask();
    }
}
