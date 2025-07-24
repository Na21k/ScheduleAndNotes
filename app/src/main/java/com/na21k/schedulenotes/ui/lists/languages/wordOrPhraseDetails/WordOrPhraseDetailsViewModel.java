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

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

public class WordOrPhraseDetailsViewModel extends ViewModel {

    @NonNull
    private final LanguagesListRepository mLanguagesListRepository;
    private final int mItemId;
    @NonNull
    private final LiveData<LanguagesListItem> mItem;
    @NonNull
    private final LanguagesListAttachedImagesRepository mLanguagesListAttachedImagesRepository;
    @NonNull
    private final LiveData<List<LanguagesListItemAttachedImage>> mAttachedImages;
    private List<LanguagesListItemAttachedImage> mImagesBefore = new ArrayList<>();
    private List<LanguagesListItemAttachedImage> mImagesAfter = new ArrayList<>();
    private boolean mTrackedAttachedImagesSetExternally;

    private WordOrPhraseDetailsViewModel(
            @NonNull LanguagesListRepository languagesListRepository,
            int itemId,
            @NonNull LanguagesListAttachedImagesRepository languagesListAttachedImagesRepository
    ) {
        super();

        mLanguagesListRepository = languagesListRepository;
        mLanguagesListAttachedImagesRepository = languagesListAttachedImagesRepository;
        mItemId = itemId;

        mItem = languagesListRepository.getById(itemId);
        mAttachedImages = languagesListAttachedImagesRepository.getByListItemId(itemId);
    }

    public boolean isEditing() {
        return mItemId != 0;
    }

    public Task<Boolean> isArchiveEmpty() {
        return mLanguagesListRepository.isArchiveEmpty();
    }

    @NonNull
    public LiveData<LanguagesListItem> getItem() {
        return mItem;
    }

    @NonNull
    public LiveData<List<LanguagesListItemAttachedImage>> getAttachedImages() {
        return mAttachedImages;
    }

    public void save(@NonNull LanguagesListItem item) {
        item.setId(mItemId);

        if (isEditing()) {
            mLanguagesListRepository.update(item);
            updateImagesIfChanged(mItemId);
        } else {
            mLanguagesListRepository.add(item)
                    .addOnSuccessListener(id -> {
                        int newItemId = Math.toIntExact(id);

                        item.setId(newItemId);
                        updateImagesIfChanged(newItemId);
                    });
        }
    }

    public void archive(@NonNull LanguagesListItem item) {
        item.setArchived(true);
        save(item);
    }

    public void unarchive(@NonNull LanguagesListItem item) {
        item.setArchived(false);
        save(item);
    }

    public void delete() {
        mLanguagesListRepository.delete(mItemId);
    }

    public void setTrackedAttachedImages(@NonNull List<LanguagesListItemAttachedImage> images) {
        mImagesBefore = images;
        mImagesAfter = new ArrayList<>(images);
        mTrackedAttachedImagesSetExternally = true;
    }

    public boolean trackedAttachedImagesSetExternally() {
        return mTrackedAttachedImagesSetExternally;
    }

    public List<LanguagesListItemAttachedImage> getTrackedAttachedImages() {
        return mImagesAfter;
    }

    public int getTrackedAttachedImagesCount() {
        return mImagesAfter.size();
    }

    public void trackAddAttachedImage(LanguagesListItemAttachedImage attachedImage) {
        mImagesAfter.add(attachedImage);
    }

    public void trackDeleteAttachedImage(LanguagesListItemAttachedImage attachedImage) {
        mImagesAfter.remove(attachedImage);
    }

    private void updateImagesIfChanged(int addedImagesListItemId) {
        if (!imagesBeforeEqualsAfter()) {
            List<LanguagesListItemAttachedImage> deleted = getDeletedImages();
            List<LanguagesListItemAttachedImage> added = getAddedImages();

            for (LanguagesListItemAttachedImage image : deleted) {
                mLanguagesListAttachedImagesRepository.delete(image);
            }

            for (LanguagesListItemAttachedImage image : added) {
                image.setLanguagesListItemId(addedImagesListItemId);
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
        private final int mItemId;

        @AssistedInject
        public Factory(
                @NonNull LanguagesListRepository languagesListRepository,
                @Assisted int itemId,
                @NonNull LanguagesListAttachedImagesRepository languagesListAttachedImagesRepository
        ) {
            mLanguagesListRepository = languagesListRepository;
            mLanguagesListAttachedImagesRepository = languagesListAttachedImagesRepository;
            mItemId = itemId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            WordOrPhraseDetailsViewModel vm = new WordOrPhraseDetailsViewModel(
                    mLanguagesListRepository, mItemId, mLanguagesListAttachedImagesRepository
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }

        @dagger.assisted.AssistedFactory
        public interface AssistedFactory {

            Factory create(int itemId);
        }
    }
}
