package com.na21k.schedulenotes.ui.lists.shopping;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.lists.shopping.ShoppingListRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class ShoppingListViewModel extends ViewModel {

    @NonNull
    private final MutableRepository<ShoppingListItem> mMutableShoppingListRepository;
    @NonNull
    private final ShoppingListRepository mShoppingListRepository;
    @NonNull
    private final CanSearchRepository<ShoppingListItem> mCanSearchShoppingListRepository;
    @NonNull
    private final CanClearRepository<ShoppingListItem> mCanClearShoppingListRepository;

    private ShoppingListViewModel(
            @NonNull MutableRepository<ShoppingListItem> mutableShoppingListRepository,
            @NonNull ShoppingListRepository shoppingListRepository,
            @NonNull CanSearchRepository<ShoppingListItem> canSearchShoppingListRepository,
            @NonNull CanClearRepository<ShoppingListItem> canClearShoppingListRepository
    ) {
        super();

        mMutableShoppingListRepository = mutableShoppingListRepository;
        mShoppingListRepository = shoppingListRepository;
        mCanSearchShoppingListRepository = canSearchShoppingListRepository;
        mCanClearShoppingListRepository = canClearShoppingListRepository;
    }

    public LiveData<List<ShoppingListItem>> getAll() {
        return mMutableShoppingListRepository.getAll();
    }

    public LiveData<List<ShoppingListItem>> getItemsSearch(String searchQuery) {
        return mCanSearchShoppingListRepository.getSearch(searchQuery);
    }

    public void addNew(ShoppingListItem item) {
        mMutableShoppingListRepository.add(item);
    }

    public void update(ShoppingListItem item) {
        mMutableShoppingListRepository.update(item);
    }

    public void delete(ShoppingListItem item) {
        mMutableShoppingListRepository.delete(item);
    }

    public void deleteChecked() {
        mShoppingListRepository.deleteChecked();
    }

    public void deleteAll() {
        mCanClearShoppingListRepository.clear();
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final MutableRepository<ShoppingListItem> mMutableShoppingListRepository;
        @NonNull
        private final ShoppingListRepository mShoppingListRepository;
        @NonNull
        private final CanSearchRepository<ShoppingListItem> mCanSearchShoppingListRepository;
        @NonNull
        private final CanClearRepository<ShoppingListItem> mCanClearShoppingListRepository;

        @Inject
        public Factory(
                @NonNull MutableRepository<ShoppingListItem> mutableShoppingListRepository,
                @NonNull ShoppingListRepository shoppingListRepository,
                @NonNull CanSearchRepository<ShoppingListItem> canSearchShoppingListRepository,
                @NonNull CanClearRepository<ShoppingListItem> canClearShoppingListRepository
        ) {
            mMutableShoppingListRepository = mutableShoppingListRepository;
            mShoppingListRepository = shoppingListRepository;
            mCanSearchShoppingListRepository = canSearchShoppingListRepository;
            mCanClearShoppingListRepository = canClearShoppingListRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            ShoppingListViewModel vm = new ShoppingListViewModel(
                    mMutableShoppingListRepository, mShoppingListRepository,
                    mCanSearchShoppingListRepository, mCanClearShoppingListRepository
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
