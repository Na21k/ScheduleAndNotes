package com.na21k.schedulenotes.ui.lists;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListDao;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;

import java.util.List;

public class ListsViewModel extends AndroidViewModel {

    private final UserDefinedListDao mListDao;
    private final LiveData<List<UserDefinedList>> mAllLists;
    private final LiveData<List<UserDefinedListItem>> mAllListItems;
    private List<UserDefinedList> mListsCache = null;
    private List<UserDefinedListItem> mListItemsCache = null;

    public ListsViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mListDao = db.userDefinedListDao();
        mAllLists = mListDao.getAll();
        mAllListItems = db.userDefinedListItemDao().getAll();
    }

    public LiveData<List<UserDefinedList>> getAllLists() {
        return mAllLists;
    }

    public LiveData<List<UserDefinedList>> getListsSearch(String searchQuery) {
        return mListDao.search(searchQuery);
    }

    public LiveData<List<UserDefinedListItem>> getAllListItems() {
        return mAllListItems;
    }

    public void addNew(UserDefinedList list) {
        new Thread(() -> mListDao.insert(list)).start();
    }

    public void update(UserDefinedList list, Thread.UncaughtExceptionHandler exceptionHandler) {
        Thread t = new Thread(() -> mListDao.update(list));
        t.setUncaughtExceptionHandler(exceptionHandler);
        t.start();
    }

    public void delete(UserDefinedList list) {
        new Thread(() -> mListDao.delete(list)).start();
    }

    public List<UserDefinedList> getListsCache() {
        return mListsCache;
    }

    public void setListsCache(List<UserDefinedList> listsCache) {
        mListsCache = listsCache;
    }

    public List<UserDefinedListItem> getListItemsCache() {
        return mListItemsCache;
    }

    public void setListItemsCache(List<UserDefinedListItem> listItemsCache) {
        mListItemsCache = listItemsCache;
    }
}
