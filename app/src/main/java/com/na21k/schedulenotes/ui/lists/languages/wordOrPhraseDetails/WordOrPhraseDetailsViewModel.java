package com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;
import com.na21k.schedulenotes.repositories.LanguagesListAttachedImagesRepository;
import com.na21k.schedulenotes.repositories.LanguagesListRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WordOrPhraseDetailsViewModel extends AndroidViewModel {

    private final LanguagesListRepository mLanguagesListRepository;
    private final LanguagesListAttachedImagesRepository mAttachedImagesRepository;
    private List<LanguagesListItemAttachedImage> mImagesBefore = new ArrayList<>();
    private List<LanguagesListItemAttachedImage> mImagesAfter = new ArrayList<>();
    private boolean mIsLoadingAttachedImages = true;
    private int mItemId;

    public WordOrPhraseDetailsViewModel(@NonNull Application application) {
        super(application);

        mLanguagesListRepository = new LanguagesListRepository(application);
        mAttachedImagesRepository = new LanguagesListAttachedImagesRepository(application);
    }

    public LiveData<LanguagesListItem> getById(int id) {
        mItemId = id;
        return mLanguagesListRepository.getById(id);
    }

    public void addNew(LanguagesListItem item) {
        mLanguagesListRepository.add(item)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mItemId = Math.toIntExact(task.getResult());
                        updateImagesIfChanged();
                    }
                });
    }

    public void update(LanguagesListItem item) {
        mLanguagesListRepository.update(item);
        updateImagesIfChanged();
    }

    public void delete(LanguagesListItem item) {
        mLanguagesListRepository.delete(item);
    }

    public LiveData<List<LanguagesListItemAttachedImage>> getAttachedImagesByItemId(int itemId) {
        return mAttachedImagesRepository.getByListItemId(itemId);
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
                mAttachedImagesRepository.delete(image);
            }

            for (LanguagesListItemAttachedImage image : added) {
                image.setLanguagesListItemId(mItemId);
                mAttachedImagesRepository.add(image);
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
