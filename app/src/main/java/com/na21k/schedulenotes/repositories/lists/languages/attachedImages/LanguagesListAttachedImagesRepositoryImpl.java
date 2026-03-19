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

// TODO: delete this class and perform all image-related operations in LanguagesListRepositoryImpl
// (including deletion of respective images as a Languages List item is deleted).
// Add a method to the Languages List item db model that will return
// a List of paths of the images attached to that item.
// The List is generated on each call and isn't a member of the class so it doesn't change its state
// (thus LiveData shouldn't be expected to be updated when an image is added/deleted).
// LanguagesListRepositoryImpl should have a method to retrieve
// a map of items and the number (count) of their attached images.
@Deprecated(forRemoval = true)
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
