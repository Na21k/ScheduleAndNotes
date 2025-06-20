package com.na21k.schedulenotes.repositories.lists.languages;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImageDao;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

import javax.inject.Inject;

public class LanguagesListAttachedImagesRepository
        extends MutableRepository<LanguagesListItemAttachedImage> {

    private final LanguagesListItemAttachedImageDao mAttachedImageDao =
            db.languagesListItemAttachedImageDao();

    @Inject
    public LanguagesListAttachedImagesRepository(@NonNull Context context) {
        super(context);
    }

    public LiveData<List<LanguagesListItemAttachedImage>> getByListItemId(int listItemId) {
        return mAttachedImageDao.getByListItemId(listItemId);
    }

    public List<LanguagesListItemAttachedImage> getByListItemIdBlocking(int listItemId) {
        return mAttachedImageDao.getByListItemIdBlocking(listItemId);
    }

    public LiveData<List<Integer>> getAllListItemIds() {
        return mAttachedImageDao.getAllListItemIds();
    }

    @Override
    protected BaseDao<LanguagesListItemAttachedImage> getDao() {
        return mAttachedImageDao;
    }
}
