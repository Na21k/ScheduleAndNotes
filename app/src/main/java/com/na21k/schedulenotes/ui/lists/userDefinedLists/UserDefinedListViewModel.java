package com.na21k.schedulenotes.ui.lists.userDefinedLists;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListItemsRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

public class UserDefinedListViewModel extends ViewModel {

    @NonNull
    private final UserDefinedListItemsRepository mUserDefinedListItemsRepository;
    private final int mListId;
    @NonNull
    private final LiveData<List<UserDefinedListItem>> mItems;

    private UserDefinedListViewModel(
            @NonNull UserDefinedListItemsRepository userDefinedListItemsRepository,
            int listId
    ) {
        super();

        mUserDefinedListItemsRepository = userDefinedListItemsRepository;
        mListId = listId;

        mItems = userDefinedListItemsRepository.getAllForList(listId);
    }

    @NonNull
    public LiveData<List<UserDefinedListItem>> getItems() {
        return mItems;
    }

    @NonNull
    public LiveData<List<UserDefinedListItem>> getItemsSearch(String searchQuery) {
        return mUserDefinedListItemsRepository.getSearch(mListId, searchQuery);
    }

    public void addNew(UserDefinedListItem userDefinedListItem) {
        userDefinedListItem.setListId(mListId);

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
        private final int mListId;

        @AssistedInject
        public Factory(@NonNull UserDefinedListItemsRepository userDefinedListItemsRepository,
                       @Assisted int listId) {
            mUserDefinedListItemsRepository = userDefinedListItemsRepository;
            mListId = listId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            UserDefinedListViewModel vm = new UserDefinedListViewModel(
                    mUserDefinedListItemsRepository, mListId
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }

        @dagger.assisted.AssistedFactory
        public interface AssistedFactory {

            Factory create(int listId);
        }
    }
}
