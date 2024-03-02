package com.na21k.schedulenotes.ui.lists.languages;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListAttachedImagesRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListRepository;

import java.util.List;

public class LanguagesListViewModel extends AndroidViewModel {

    private final LanguagesListRepository mLanguagesListRepository;
    private final LiveData<List<Integer>> mAllAttachedImagesListItemIds;
    private List<LanguagesListItem> mAllItemsCache;
    private List<Integer> mAttachedImagesListItemIdsCache;

    public LanguagesListViewModel(@NonNull Application application) {
        super(application);

        mLanguagesListRepository = new LanguagesListRepository(application);
        mAllAttachedImagesListItemIds = new LanguagesListAttachedImagesRepository(application)
                .getAllListItemIds();
    }

    public LiveData<List<LanguagesListItem>> getAll() {
        return mLanguagesListRepository.getAll();
    }

    public LiveData<List<LanguagesListItem>> getItemsSearch(String searchQuery) {
        return mLanguagesListRepository.getSearch(searchQuery);
    }

    public void delete(LanguagesListItem item) {
        mLanguagesListRepository.delete(item);
    }

    public LiveData<List<Integer>> getAllAttachedImagesListItemIds() {
        return mAllAttachedImagesListItemIds;
    }

    public List<LanguagesListItem> getAllItemsCache() {
        return mAllItemsCache;
    }

    public void setAllItemsCache(List<LanguagesListItem> allItemsCache) {
        mAllItemsCache = allItemsCache;
    }

    public List<Integer> getAttachedImagesListItemIdsCache() {
        return mAttachedImagesListItemIdsCache;
    }

    public void setAttachedImagesListItemIdsCache(List<Integer> attachedImagesListItemIdsCache) {
        mAttachedImagesListItemIdsCache = attachedImagesListItemIdsCache;
    }
}
