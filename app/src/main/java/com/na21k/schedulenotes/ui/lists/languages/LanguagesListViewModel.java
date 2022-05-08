package com.na21k.schedulenotes.ui.lists.languages;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemDao;

import java.util.List;

public class LanguagesListViewModel extends AndroidViewModel {

    private final LanguagesListItemDao mLanguagesListItemDao;
    private final LiveData<List<LanguagesListItem>> mAllItems;
    private final LiveData<List<Integer>> mAllAttachedImagesListItemIds;
    private List<LanguagesListItem> mAllItemsCache;
    private List<Integer> mAttachedImagesListItemIdsCache;

    public LanguagesListViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mLanguagesListItemDao = db.languagesListItemDao();
        mAllItems = mLanguagesListItemDao.getAll();
        mAllAttachedImagesListItemIds = db.languagesListItemAttachedImageDao().getAllListItemIds();
    }

    public LiveData<List<LanguagesListItem>> getAll() {
        return mAllItems;
    }

    public LiveData<List<LanguagesListItem>> getItemsSearch(String searchQuery) {
        return mLanguagesListItemDao.search(searchQuery);
    }

    public void addNew(LanguagesListItem item) {
        new Thread(() -> mLanguagesListItemDao.insert(item)).start();
    }

    public void delete(LanguagesListItem item) {
        new Thread(() -> mLanguagesListItemDao.delete(item)).start();
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
