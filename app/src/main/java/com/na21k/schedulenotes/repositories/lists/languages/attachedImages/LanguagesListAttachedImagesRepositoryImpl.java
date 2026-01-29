package com.na21k.schedulenotes.repositories.lists.languages.attachedImages;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImageDao;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

import javax.inject.Inject;

public class LanguagesListAttachedImagesRepositoryImpl
        extends MutableRepository<LanguagesListItemAttachedImage>
        implements LanguagesListAttachedImagesRepository {

    private final LanguagesListItemAttachedImageDao mAttachedImageDao =
            db.languagesListItemAttachedImageDao();

    @Inject
    public LanguagesListAttachedImagesRepositoryImpl(@NonNull Context context) {
        super(context);
    }

    @Override
    protected BaseDao<LanguagesListItemAttachedImage> getDao() {
        return mAttachedImageDao;
    }

    @Override
    public LiveData<List<LanguagesListItemAttachedImage>> getByListItemId(int listItemId) {
        return mAttachedImageDao.getByListItemId(listItemId);
    }

    @Override
    public List<LanguagesListItemAttachedImage> getByListItemIdBlocking(int listItemId) {
        return mAttachedImageDao.getByListItemIdBlocking(listItemId);
    }

    @Override
    public LiveData<List<Integer>> getAllListItemIds() {
        return mAttachedImageDao.getAllListItemIds();
    }
}
