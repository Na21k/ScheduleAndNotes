package com.na21k.schedulenotes.repositories;

import android.content.Context;

import androidx.annotation.NonNull;

import com.na21k.schedulenotes.data.database.AppDatabase;

public abstract class Repository {

    protected final AppDatabase db;

    protected Repository(@NonNull Context context) {
        db = AppDatabase.getInstance(context);
    }
}
