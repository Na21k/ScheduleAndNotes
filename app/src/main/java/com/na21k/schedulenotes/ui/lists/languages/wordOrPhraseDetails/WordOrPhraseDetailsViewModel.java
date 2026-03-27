package com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

public class WordOrPhraseDetailsViewModel extends ViewModel {

    @NonNull
    private final MutableRepository<LanguagesListItem> mMutableLanguagesListRepository;
    @NonNull
    private final LanguagesListRepository mLanguagesListRepository;
    private final int mItemId;
    @NonNull
    private final LiveData<LanguagesListItem> mItem;
    private List<String> mImagesBefore = new ArrayList<>();
    private List<String> mImagesAfter = new ArrayList<>();
    private boolean mTrackedAttachedImagesSetExternally;

    private WordOrPhraseDetailsViewModel(
            @NonNull MutableRepository<LanguagesListItem> mutableLanguagesListRepository,
            @NonNull LanguagesListRepository languagesListRepository,
            int itemId
    ) {
        super();

        mMutableLanguagesListRepository = mutableLanguagesListRepository;
        mLanguagesListRepository = languagesListRepository;
        mItemId = itemId;
        mItem = mutableLanguagesListRepository.getById(itemId);
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

    public void save(@NonNull LanguagesListItem item) {
        item.setId(mItemId);

        if (isEditing()) {
            mMutableLanguagesListRepository.update(item);
            updateImagesIfChanged(mItemId);
        } else {
            mMutableLanguagesListRepository.add(item)
                    .addOnSuccessListener(id -> {
                        int newItemId = Math.toIntExact(id);

                        item.setId(newItemId);
                        updateImagesIfChanged(newItemId);
                    });
        }
    }

    public void setArchived(boolean archived) {
        mLanguagesListRepository.setArchived(mItemId, archived);
    }

    public void delete() {
        mMutableLanguagesListRepository.delete(mItemId);
    }

    public void setTrackedAttachedImages(@NonNull List<String> absoluteImagePaths) {
        mImagesBefore = absoluteImagePaths;
        mImagesAfter = new ArrayList<>(absoluteImagePaths);
        mTrackedAttachedImagesSetExternally = true;
    }

    public boolean trackedAttachedImagesSetExternally() {
        return mTrackedAttachedImagesSetExternally;
    }

    public List<String> getTrackedAttachedImages() {
        return mImagesAfter;
    }

    public int getTrackedAttachedImagesCount() {
        return mImagesAfter.size();
    }

    public void trackAddAttachedImage(String attachedImagePath) {
        mImagesAfter.add(attachedImagePath);
    }

    public void trackDeleteAttachedImage(String attachedImagePath) {
        mImagesAfter.remove(attachedImagePath);
    }

    private void updateImagesIfChanged(int addedImagesListItemId) {
        if (!imagesBeforeEqualsAfter()) {
            List<String> deleted = getDeletedImages();
            List<String> added = getAddedImages();

            for (String image : deleted) {
                mLanguagesListRepository.deleteAttachedImage(image);
            }

            for (String image : added) {
                mLanguagesListRepository.addAttachedImage(addedImagesListItemId, image);
            }
        }
    }

    private boolean imagesBeforeEqualsAfter() {
        return Objects.equals(mImagesBefore, mImagesAfter);
    }

    private List<String> getDeletedImages() {
        List<String> deleted = new ArrayList<>();

        for (String image : mImagesBefore) {
            if (!mImagesAfter.contains(image)) {
                deleted.add(image);
            }
        }

        return deleted;
    }

    private List<String> getAddedImages() {
        List<String> added = new ArrayList<>();

        for (String image : mImagesAfter) {
            if (!mImagesBefore.contains(image)) {
                added.add(image);
            }
        }

        return added;
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final MutableRepository<LanguagesListItem> mMutableLanguagesListRepository;
        @NonNull
        private final LanguagesListRepository mLanguagesListRepository;
        private final int mItemId;

        @AssistedInject
        public Factory(
                @NonNull MutableRepository<LanguagesListItem> mutableLanguagesListRepository,
                @NonNull LanguagesListRepository languagesListRepository,
                @Assisted int itemId
        ) {
            mMutableLanguagesListRepository = mutableLanguagesListRepository;
            mLanguagesListRepository = languagesListRepository;
            mItemId = itemId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            WordOrPhraseDetailsViewModel vm = new WordOrPhraseDetailsViewModel(
                    mMutableLanguagesListRepository, mLanguagesListRepository, mItemId
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
