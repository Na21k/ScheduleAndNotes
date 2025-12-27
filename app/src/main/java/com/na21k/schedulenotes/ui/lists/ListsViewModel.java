package com.na21k.schedulenotes.ui.lists;

import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListItemsRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class ListsViewModel extends ViewModel {

    @NonNull
    private final MutableRepository<UserDefinedList> mMutableListsRepository;
    @NonNull
    private final CanSearchRepository<UserDefinedList> mCanSearchListsRepository;
    @NonNull
    private final UserDefinedListItemsRepository mUserDefinedListItemsRepository;
    private List<UserDefinedList> mListsCache = null;
    private List<UserDefinedListItem> mListItemsCache = null;

    private ListsViewModel(
            @NonNull MutableRepository<UserDefinedList> mutableUserDefinedListsRepository,
            @NonNull CanSearchRepository<UserDefinedList> canSearchUserDefinedListsRepository,
            @NonNull UserDefinedListItemsRepository userDefinedListItemsRepository
    ) {
        super();

        mMutableListsRepository = mutableUserDefinedListsRepository;
        mCanSearchListsRepository = canSearchUserDefinedListsRepository;
        mUserDefinedListItemsRepository = userDefinedListItemsRepository;
    }

    public LiveData<List<UserDefinedList>> getAllLists() {
        return mMutableListsRepository.getAll();
    }

    public LiveData<List<UserDefinedList>> getListsSearch(String searchQuery) {
        return mCanSearchListsRepository.getSearch(searchQuery);
    }

    public LiveData<List<UserDefinedListItem>> getAllListItems() {
        return mUserDefinedListItemsRepository.getAll();
    }

    public void addNew(UserDefinedList list) {
        mMutableListsRepository.add(list);
    }

    public void update(UserDefinedList list, Runnable onSQLiteConstraintException) {
        mMutableListsRepository.update(list).addOnFailureListener(e -> {
            if (e.getClass().equals(SQLiteConstraintException.class)) {
                onSQLiteConstraintException.run();
            } else {
                throw new RuntimeException(e);
            }
        });
    }

    public void delete(UserDefinedList list) {
        mMutableListsRepository.delete(list);
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
        private final MutableRepository<UserDefinedList> mMutableListsRepository;
        @NonNull
        private final CanSearchRepository<UserDefinedList> mCanSearchListsRepository;
        @NonNull
        private final UserDefinedListItemsRepository mUserDefinedListItemsRepository;

        @Inject
        public Factory(
                @NonNull MutableRepository<UserDefinedList> mutableUserDefinedListsRepository,
                @NonNull CanSearchRepository<UserDefinedList> canSearchUserDefinedListsRepository,
                @NonNull UserDefinedListItemsRepository userDefinedListItemsRepository
        ) {
            mMutableListsRepository = mutableUserDefinedListsRepository;
            mCanSearchListsRepository = canSearchUserDefinedListsRepository;
            mUserDefinedListItemsRepository = userDefinedListItemsRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            ListsViewModel vm = new ListsViewModel(
                    mMutableListsRepository, mCanSearchListsRepository,
                    mUserDefinedListItemsRepository
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
