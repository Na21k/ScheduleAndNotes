package com.na21k.schedulenotes.ui.lists.userDefinedLists;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListItemsRepository;

import java.util.List;

public class UserDefinedListViewModel extends AndroidViewModel {

    private final UserDefinedListItemsRepository mUserDefinedListItemsRepository;
    private Integer mListId = null;

    public UserDefinedListViewModel(@NonNull Application application) {
        super(application);

        mUserDefinedListItemsRepository = new UserDefinedListItemsRepository(application);
    }

    public void configure(int listId) {
        mListId = listId;
    }

    private void ensureConfigured() {
        if (mListId == null) {
            throw new IllegalStateException("The ViewModel has not been configured.");
        }
    }

    public LiveData<List<UserDefinedListItem>> getItems() {
        ensureConfigured();
        return mUserDefinedListItemsRepository.getAllForList(mListId);
    }

    public LiveData<List<UserDefinedListItem>> getItemsSearch(String searchQuery) {
        ensureConfigured();
        return mUserDefinedListItemsRepository.getSearch(mListId, searchQuery);
    }

    public void addNew(UserDefinedListItem userDefinedListItem) {
        mUserDefinedListItemsRepository.add(userDefinedListItem)
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public void update(UserDefinedListItem userDefinedListItem) {
        mUserDefinedListItemsRepository.update(userDefinedListItem)
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public void delete(UserDefinedListItem userDefinedListItem) {
        mUserDefinedListItemsRepository.delete(userDefinedListItem)
                .addOnFailureListener(Throwable::printStackTrace);
    }
}
