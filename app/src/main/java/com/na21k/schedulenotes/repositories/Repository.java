package com.na21k.schedulenotes.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Identifiable;

import java.util.List;

public abstract class Repository<T extends Identifiable> {

    protected final AppDatabase db;

    protected Repository(@NonNull Context context) {
        db = AppDatabase.getInstance(context);
    }

    public LiveData<List<T>> getAll() {
        return getDao().getAll();
    }

    public List<T> getAllBlocking() {
        return getDao().getAllBlocking();
    }

    public LiveData<T> getById(int itemId) {
        return getDao().getById(itemId);
    }

    public T getByIdBlocking(int itemId) {
        return getDao().getByIdBlocking(itemId);
    }

    protected abstract BaseDao<T> getDao();
}
