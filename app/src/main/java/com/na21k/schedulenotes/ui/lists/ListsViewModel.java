package com.na21k.schedulenotes.ui.lists;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListItemsRepository;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListsRepository;

import java.util.List;

public class ListsViewModel extends AndroidViewModel {

    private final UserDefinedListsRepository mListsRepository;
    private final UserDefinedListItemsRepository mUserDefinedListItemsRepository;
    private List<UserDefinedList> mListsCache = null;
    private List<UserDefinedListItem> mListItemsCache = null;

    public ListsViewModel(@NonNull Application application) {
        super(application);

        mListsRepository = new UserDefinedListsRepository(application);
        mUserDefinedListItemsRepository = new UserDefinedListItemsRepository(application);
    }

    public LiveData<List<UserDefinedList>> getAllLists() {
        return mListsRepository.getAll();
    }

    public LiveData<List<UserDefinedList>> getListsSearch(String searchQuery) {
        return mListsRepository.getSearch(searchQuery);
    }

    public LiveData<List<UserDefinedListItem>> getAllListItems() {
        return mUserDefinedListItemsRepository.getAll();
    }

    public void addNew(UserDefinedList list) {
        mListsRepository.add(list);
    }

    public void update(UserDefinedList list, Runnable onSQLiteConstraintException) {
        mListsRepository.update(list).addOnFailureListener(e -> {
            if (e.getClass().equals(SQLiteConstraintException.class)) {
                onSQLiteConstraintException.run();
            } else {
                throw new RuntimeException(e);
            }
        });
    }

    public void delete(UserDefinedList list) {
        mListsRepository.delete(list);
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
