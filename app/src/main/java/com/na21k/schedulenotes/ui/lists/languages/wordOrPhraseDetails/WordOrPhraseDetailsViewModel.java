package com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListAttachedImagesRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class WordOrPhraseDetailsViewModel extends ViewModel {

    @NonNull
    private final LanguagesListRepository mLanguagesListRepository;
    @NonNull
    private final LanguagesListAttachedImagesRepository mLanguagesListAttachedImagesRepository;
    private List<LanguagesListItemAttachedImage> mImagesBefore = new ArrayList<>();
    private List<LanguagesListItemAttachedImage> mImagesAfter = new ArrayList<>();
    private boolean mIsLoadingAttachedImages = true;
    private int mItemId;

    private WordOrPhraseDetailsViewModel(
            @NonNull LanguagesListRepository languagesListRepository,
            @NonNull LanguagesListAttachedImagesRepository languagesListAttachedImagesRepository
    ) {
        super();

        mLanguagesListRepository = languagesListRepository;
        mLanguagesListAttachedImagesRepository = languagesListAttachedImagesRepository;
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

    Task<Boolean> isArchiveEmpty() {
        return mLanguagesListRepository.isArchiveEmpty();
    }

    public void archive(LanguagesListItem item) {
        item.setArchived(true);
        update(item);
    }

    public void unarchive(LanguagesListItem item) {
        item.setArchived(false);
        update(item);
    }

    public void delete(LanguagesListItem item) {
        mLanguagesListRepository.delete(item);
    }

    public LiveData<List<LanguagesListItemAttachedImage>> getAttachedImagesByItemId(int itemId) {
        return mLanguagesListAttachedImagesRepository.getByListItemId(itemId);
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
                mLanguagesListAttachedImagesRepository.delete(image);
            }

            for (LanguagesListItemAttachedImage image : added) {
                image.setLanguagesListItemId(mItemId);
                mLanguagesListAttachedImagesRepository.add(image);
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

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final LanguagesListRepository mLanguagesListRepository;
        @NonNull
        private final LanguagesListAttachedImagesRepository mLanguagesListAttachedImagesRepository;

        @Inject
        public Factory(
                @NonNull LanguagesListRepository languagesListRepository,
                @NonNull LanguagesListAttachedImagesRepository languagesListAttachedImagesRepository
        ) {
            mLanguagesListRepository = languagesListRepository;
            mLanguagesListAttachedImagesRepository = languagesListAttachedImagesRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            WordOrPhraseDetailsViewModel vm = new WordOrPhraseDetailsViewModel(
                    mLanguagesListRepository, mLanguagesListAttachedImagesRepository
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
