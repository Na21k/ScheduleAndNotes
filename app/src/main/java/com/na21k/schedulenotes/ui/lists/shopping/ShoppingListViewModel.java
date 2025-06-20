package com.na21k.schedulenotes.ui.lists.shopping;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.repositories.lists.ShoppingListRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class ShoppingListViewModel extends ViewModel {

    @NonNull
    private final ShoppingListRepository mShoppingListRepository;

    private ShoppingListViewModel(@NonNull ShoppingListRepository shoppingListRepository) {
        super();

        mShoppingListRepository = shoppingListRepository;
    }

    public LiveData<List<ShoppingListItem>> getAll() {
        return mShoppingListRepository.getAll();
    }

    public LiveData<List<ShoppingListItem>> getItemsSearch(String searchQuery) {
        return mShoppingListRepository.getSearch(searchQuery);
    }

    public void addNew(ShoppingListItem item) {
        mShoppingListRepository.add(item);
    }

    public void update(ShoppingListItem item) {
        mShoppingListRepository.update(item);
    }

    public void delete(ShoppingListItem item) {
        mShoppingListRepository.delete(item);
    }

    public void deleteChecked() {
        mShoppingListRepository.deleteChecked();
    }

    public void deleteAll() {
        mShoppingListRepository.clear();
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final ShoppingListRepository mShoppingListRepository;

        @Inject
        public Factory(@NonNull ShoppingListRepository shoppingListRepository) {
            mShoppingListRepository = shoppingListRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            ShoppingListViewModel vm = new ShoppingListViewModel(mShoppingListRepository);
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
