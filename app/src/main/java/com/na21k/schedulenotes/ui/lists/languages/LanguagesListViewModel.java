package com.na21k.schedulenotes.ui.lists.languages;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListAttachedImagesRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class LanguagesListViewModel extends ViewModel {

    @NonNull
    private final MutableRepository<LanguagesListItem> mMutableLanguagesListRepository;
    @NonNull
    private final LanguagesListRepository mLanguagesListRepository;
    @NonNull
    private final CanSearchRepository<LanguagesListItem> mCanSearchLanguagesListRepository;
    private final LiveData<List<LanguagesListItem>> mUnarchivedItems;
    private final LiveData<List<LanguagesListItem>> mArchivedItems;
    private final LiveData<List<Integer>> mAllAttachedImagesListItemIds;
    private List<LanguagesListItem> mDisplayedItemsCache;
    private List<Integer> mDisplayedItemsAttachedImagesListItemIdsCache;

    private LanguagesListViewModel(
            @NonNull MutableRepository<LanguagesListItem> mutableLanguagesListRepository,
            @NonNull LanguagesListRepository languagesListRepository,
            @NonNull CanSearchRepository<LanguagesListItem> canSearchLanguagesListRepository,
            @NonNull LanguagesListAttachedImagesRepository languagesListAttachedImagesRepository
    ) {
        super();

        mMutableLanguagesListRepository = mutableLanguagesListRepository;
        mLanguagesListRepository = languagesListRepository;
        mCanSearchLanguagesListRepository = canSearchLanguagesListRepository;

        mUnarchivedItems = languagesListRepository.getUnarchived();
        mArchivedItems = languagesListRepository.getArchived();
        mAllAttachedImagesListItemIds = languagesListAttachedImagesRepository.getAllListItemIds();
    }

    public LiveData<List<LanguagesListItem>> getUnarchived() {
        return mUnarchivedItems;
    }

    public LiveData<List<LanguagesListItem>> getArchived() {
        return mArchivedItems;
    }

    public LiveData<List<LanguagesListItem>> getItemsSearch(String searchQuery) {
        return mCanSearchLanguagesListRepository.getSearch(searchQuery);
    }

    Task<Boolean> isArchiveEmpty() {
        return mLanguagesListRepository.isArchiveEmpty();
    }

    public void archive(LanguagesListItem item) {
        mLanguagesListRepository.setArchived(item.getId(), true);
    }

    public void unarchive(LanguagesListItem item) {
        mLanguagesListRepository.setArchived(item.getId(), false);
    }

    public void delete(LanguagesListItem item) {
        mMutableLanguagesListRepository.delete(item);
    }

    public LiveData<List<Integer>> getAllAttachedImagesListItemIds() {
        return mAllAttachedImagesListItemIds;
    }

    public List<LanguagesListItem> getDisplayedItemsCache() {
        return mDisplayedItemsCache;
    }

    public void setDisplayedItemsCache(List<LanguagesListItem> allItemsCache) {
        mDisplayedItemsCache = allItemsCache;
    }

    public List<Integer> getDisplayedItemsAttachedImagesListItemIdsCache() {
        return mDisplayedItemsAttachedImagesListItemIdsCache;
    }

    public void setDisplayedItemsAttachedImagesListItemIdsCache(
            List<Integer> attachedImagesListItemIdsCache) {
        mDisplayedItemsAttachedImagesListItemIdsCache = attachedImagesListItemIdsCache;
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final MutableRepository<LanguagesListItem> mMutableLanguagesListRepository;
        @NonNull
        private final LanguagesListRepository mLanguagesListRepository;
        @NonNull
        private final CanSearchRepository<LanguagesListItem> mCanSearchLanguagesListRepository;
        @NonNull
        private final LanguagesListAttachedImagesRepository mLanguagesListAttachedImagesRepository;

        @Inject
        public Factory(
                @NonNull MutableRepository<LanguagesListItem> mutableLanguagesListRepository,
                @NonNull LanguagesListRepository languagesListRepository,
                @NonNull CanSearchRepository<LanguagesListItem> canSearchLanguagesListRepository,
                @NonNull LanguagesListAttachedImagesRepository languagesListAttachedImagesRepository
        ) {
            mMutableLanguagesListRepository = mutableLanguagesListRepository;
            mLanguagesListRepository = languagesListRepository;
            mCanSearchLanguagesListRepository = canSearchLanguagesListRepository;
            mLanguagesListAttachedImagesRepository = languagesListAttachedImagesRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            LanguagesListViewModel vm = new LanguagesListViewModel(
                    mMutableLanguagesListRepository, mLanguagesListRepository,
                    mCanSearchLanguagesListRepository, mLanguagesListAttachedImagesRepository
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
