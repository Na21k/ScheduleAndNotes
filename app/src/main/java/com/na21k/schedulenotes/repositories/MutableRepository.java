package com.na21k.schedulenotes.repositories;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.na21k.schedulenotes.data.database.Identifiable;

import java.util.List;

public abstract class MutableRepository<T extends Identifiable, SaveRes, DelRes>
        extends Repository<T> {

    protected MutableRepository(@NonNull Context context) {
        super(context);
    }

    public abstract Task<SaveRes> add(T item);

    public abstract Task<SaveRes> add(List<T> items);

    public abstract Task<SaveRes> update(T item);

    public abstract Task<DelRes> delete(T item);

    public abstract Task<DelRes> deleteAll();
}
