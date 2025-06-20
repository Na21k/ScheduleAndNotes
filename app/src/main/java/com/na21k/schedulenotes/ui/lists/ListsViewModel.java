package com.na21k.schedulenotes.ui.lists;

import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListItemsRepository;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListsRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class ListsViewModel extends ViewModel {

    @NonNull
    private final UserDefinedListsRepository mListsRepository;
    @NonNull
    private final UserDefinedListItemsRepository mUserDefinedListItemsRepository;
    private List<UserDefinedList> mListsCache = null;
    private List<UserDefinedListItem> mListItemsCache = null;

    private ListsViewModel(
            @NonNull UserDefinedListsRepository userDefinedListsRepository,
            @NonNull UserDefinedListItemsRepository userDefinedListItemsRepository
    ) {
        super();

        mListsRepository = userDefinedListsRepository;
        mUserDefinedListItemsRepository = userDefinedListItemsRepository;
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

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final UserDefinedListsRepository mListsRepository;
        @NonNull
        private final UserDefinedListItemsRepository mUserDefinedListItemsRepository;

        @Inject
        public Factory(
                @NonNull UserDefinedListsRepository listsRepository,
                @NonNull UserDefinedListItemsRepository userDefinedListItemsRepository
        ) {
            mListsRepository = listsRepository;
            mUserDefinedListItemsRepository = userDefinedListItemsRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            ListsViewModel vm = new ListsViewModel(
                    mListsRepository, mUserDefinedListItemsRepository
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
