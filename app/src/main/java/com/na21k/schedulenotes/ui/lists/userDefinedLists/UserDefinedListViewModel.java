package com.na21k.schedulenotes.ui.lists.userDefinedLists;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.lists.userDefined.listItems.UserDefinedListItemsRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

public class UserDefinedListViewModel extends ViewModel {

    @NonNull
    private final MutableRepository<UserDefinedListItem> mMutableUserDefinedListItemsRepository;
    @NonNull
    private final UserDefinedListItemsRepository mUserDefinedListItemsRepository;
    private final int mListId;
    @NonNull
    private final LiveData<List<UserDefinedListItem>> mItems;

    private UserDefinedListViewModel(
            @NonNull MutableRepository<UserDefinedListItem> mutableUserDefinedListItemsRepository,
            @NonNull UserDefinedListItemsRepository userDefinedListItemsRepository,
            int listId
    ) {
        super();

        mMutableUserDefinedListItemsRepository = mutableUserDefinedListItemsRepository;
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

        mMutableUserDefinedListItemsRepository.add(userDefinedListItem)
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public void update(UserDefinedListItem userDefinedListItem) {
        mMutableUserDefinedListItemsRepository.update(userDefinedListItem)
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public void delete(UserDefinedListItem userDefinedListItem) {
        mMutableUserDefinedListItemsRepository.delete(userDefinedListItem)
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final MutableRepository<UserDefinedListItem> mMutableUserDefinedListItemsRepository;
        @NonNull
        private final UserDefinedListItemsRepository mUserDefinedListItemsRepository;
        private final int mListId;

        @AssistedInject
        public Factory(@NonNull MutableRepository<UserDefinedListItem> mutableUserDefinedListItemsRepository,
                       @NonNull UserDefinedListItemsRepository userDefinedListItemsRepository,
                       @Assisted int listId) {
            mMutableUserDefinedListItemsRepository = mutableUserDefinedListItemsRepository;
            mUserDefinedListItemsRepository = userDefinedListItemsRepository;
            mListId = listId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            UserDefinedListViewModel vm = new UserDefinedListViewModel(
                    mMutableUserDefinedListItemsRepository, mUserDefinedListItemsRepository,
                    mListId
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
