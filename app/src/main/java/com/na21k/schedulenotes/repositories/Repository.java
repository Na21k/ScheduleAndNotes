package com.na21k.schedulenotes.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Identifiable;

import java.util.List;

public abstract class Repository<T extends Identifiable> {

    protected final AppDatabase db;

    protected Repository(@NonNull Context context) {
        db = AppDatabase.getInstance(context);
    }

    public abstract LiveData<T> getById(int id);

    public abstract LiveData<List<T>> getAll();

    public abstract LiveData<List<T>> getSearch(String query);
}
