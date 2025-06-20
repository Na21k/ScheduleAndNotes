package com.na21k.schedulenotes.ui.lists.userDefinedLists;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListItemsRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class UserDefinedListViewModel extends ViewModel {

    @NonNull
    private final UserDefinedListItemsRepository mUserDefinedListItemsRepository;
    private Integer mListId = null;

    private UserDefinedListViewModel(
            @NonNull UserDefinedListItemsRepository userDefinedListItemsRepository
    ) {
        super();

        mUserDefinedListItemsRepository = userDefinedListItemsRepository;
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

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final UserDefinedListItemsRepository mUserDefinedListItemsRepository;

        @Inject
        public Factory(@NonNull UserDefinedListItemsRepository userDefinedListItemsRepository) {
            mUserDefinedListItemsRepository = userDefinedListItemsRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            UserDefinedListViewModel vm = new UserDefinedListViewModel(
                    mUserDefinedListItemsRepository
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
