package com.na21k.schedulenotes.repositories.lists.languages;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImageDao;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

public class LanguagesListAttachedImagesRepository
        extends MutableRepository<LanguagesListItemAttachedImage, Void> {

    private final LanguagesListItemAttachedImageDao mAttachedImageDao =
            db.languagesListItemAttachedImageDao();

    public LanguagesListAttachedImagesRepository(@NonNull Context context) {
        super(context);
    }

    public LiveData<List<LanguagesListItemAttachedImage>> getByListItemId(int listItemId) {
        return mAttachedImageDao.getByListItemId(listItemId);
    }

    public LiveData<List<Integer>> getAllListItemIds() {
        return mAttachedImageDao.getAllListItemIds();
    }

    @Override
    public Task<Void> add(LanguagesListItemAttachedImage item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mAttachedImageDao.insert(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> add(List<LanguagesListItemAttachedImage> items) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mAttachedImageDao.insert(items);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> update(LanguagesListItemAttachedImage item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mAttachedImageDao.update(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> delete(LanguagesListItemAttachedImage item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mAttachedImageDao.delete(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }
}
