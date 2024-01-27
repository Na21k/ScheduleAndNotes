package com.na21k.schedulenotes.repositories;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.na21k.schedulenotes.data.database.Identifiable;

import java.util.List;

public abstract class MutableRepository<T extends Identifiable, AddRes> extends Repository {

    protected MutableRepository(@NonNull Context context) {
        super(context);
    }

    public abstract Task<AddRes> add(T item);

    public abstract Task<Void> add(List<T> items);

    public abstract Task<Void> update(T item);

    public abstract Task<Void> delete(T item);
}
