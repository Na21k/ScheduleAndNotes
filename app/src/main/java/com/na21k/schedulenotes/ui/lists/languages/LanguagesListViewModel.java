package com.na21k.schedulenotes.ui.lists.languages;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListAttachedImagesRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListRepository;

import java.util.List;

public class LanguagesListViewModel extends AndroidViewModel {

    private final LanguagesListRepository mLanguagesListRepository;
    private final LiveData<List<LanguagesListItem>> mUnarchivedItems;
    private final LiveData<List<LanguagesListItem>> mArchivedItems;
    private final LiveData<List<Integer>> mAllAttachedImagesListItemIds;
    private List<LanguagesListItem> mDisplayedItemsCache;
    private List<Integer> mDisplayedItemsAttachedImagesListItemIdsCache;

    public LanguagesListViewModel(@NonNull Application application) {
        super(application);

        mLanguagesListRepository = new LanguagesListRepository(application);
        mUnarchivedItems = mLanguagesListRepository.getUnarchived();
        mArchivedItems = mLanguagesListRepository.getArchived();
        mAllAttachedImagesListItemIds = new LanguagesListAttachedImagesRepository(application)
                .getAllListItemIds();
    }

    public LiveData<List<LanguagesListItem>> getUnarchived() {
        return mUnarchivedItems;
    }

    public LiveData<List<LanguagesListItem>> getArchived() {
        return mArchivedItems;
    }

    public LiveData<List<LanguagesListItem>> getItemsSearch(String searchQuery) {
        return mLanguagesListRepository.getSearch(searchQuery);
    }

    Task<Boolean> isArchiveEmpty() {
        return mLanguagesListRepository.isArchiveEmpty();
    }

    public void archive(LanguagesListItem item) {
        item.setArchived(true);
        mLanguagesListRepository.update(item);
    }

    public void unarchive(LanguagesListItem item) {
        item.setArchived(false);
        mLanguagesListRepository.update(item);
    }

    public void delete(LanguagesListItem item) {
        mLanguagesListRepository.delete(item);
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
}
