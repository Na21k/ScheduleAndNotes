package com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImageDao;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WordOrPhraseDetailsViewModel extends AndroidViewModel {

    private final LanguagesListItemDao mLanguagesListItemDao;
    private final LanguagesListItemAttachedImageDao mAttachedImageDao;
    private List<LanguagesListItemAttachedImage> mImagesBefore = new ArrayList<>();
    private List<LanguagesListItemAttachedImage> mImagesAfter = new ArrayList<>();
    private boolean mIsLoadingAttachedImages = true;
    private int mItemId;

    public WordOrPhraseDetailsViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mLanguagesListItemDao = db.languagesListItemDao();
        mAttachedImageDao = db.languagesListItemAttachedImageDao();
    }

    public LiveData<LanguagesListItem> getById(int id) {
        mItemId = id;
        return mLanguagesListItemDao.getById(id);
    }

    public void addNew(LanguagesListItem item) {
        new Thread(() -> {
            mItemId = (int) mLanguagesListItemDao.insert(item);
            updateImagesIfChanged();
        }).start();
    }

    public void update(LanguagesListItem item) {
        new Thread(() -> mLanguagesListItemDao.update(item)).start();
        updateImagesIfChanged();
    }

    public void delete(LanguagesListItem item) {
        new Thread(() -> mLanguagesListItemDao.delete(item)).start();
    }

    public LiveData<List<LanguagesListItemAttachedImage>> getAttachedImagesByItemId(int itemId) {
        return mAttachedImageDao.getByListItemId(itemId);
    }

    public void addAttachedImage(LanguagesListItemAttachedImage attachedImage) {
        mImagesAfter.add(attachedImage);
    }

    public void deleteAttachedImage(LanguagesListItemAttachedImage attachedImage) {
        mImagesAfter.remove(attachedImage);
    }

    public void setAttachedImages(@NonNull List<LanguagesListItemAttachedImage> images) {
        mImagesBefore = images;
        mImagesAfter = new ArrayList<>(images);
        mIsLoadingAttachedImages = false;
    }

    public List<LanguagesListItemAttachedImage> getAttachedImages() {
        return mImagesAfter;
    }

    public int getAttachedImagesCount() {
        return mImagesAfter.size();
    }

    public boolean isLoadingAttachedImages() {
        return mIsLoadingAttachedImages;
    }

    private void updateImagesIfChanged() {
        if (!imagesBeforeEqualsAfter()) {
            List<LanguagesListItemAttachedImage> deleted = getDeletedImages();
            List<LanguagesListItemAttachedImage> added = getAddedImages();

            for (LanguagesListItemAttachedImage image : deleted) {
                new Thread(() -> mAttachedImageDao.delete(image)).start();
            }

            for (LanguagesListItemAttachedImage image : added) {
                image.setLanguagesListItemId(mItemId);
                new Thread(() -> mAttachedImageDao.insert(image)).start();
            }
        }
    }

    private boolean imagesBeforeEqualsAfter() {
        return Objects.equals(mImagesBefore, mImagesAfter);
    }

    private List<LanguagesListItemAttachedImage> getDeletedImages() {
        List<LanguagesListItemAttachedImage> deleted = new ArrayList<>();

        for (LanguagesListItemAttachedImage image : mImagesBefore) {
            if (!mImagesAfter.contains(image)) {
                deleted.add(image);
            }
        }

        return deleted;
    }

    private List<LanguagesListItemAttachedImage> getAddedImages() {
        List<LanguagesListItemAttachedImage> added = new ArrayList<>();

        for (LanguagesListItemAttachedImage image : mImagesAfter) {
            if (!mImagesBefore.contains(image)) {
                added.add(image);
            }
        }

        return added;
    }
}
