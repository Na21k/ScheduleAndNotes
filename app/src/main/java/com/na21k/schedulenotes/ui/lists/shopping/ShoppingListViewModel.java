package com.na21k.schedulenotes.ui.lists.shopping;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.repositories.lists.ShoppingListRepository;

import java.util.List;

public class ShoppingListViewModel extends AndroidViewModel {

    private final ShoppingListRepository mShoppingListRepository;

    public ShoppingListViewModel(@NonNull Application application) {
        super(application);

        mShoppingListRepository = new ShoppingListRepository(application);
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
}
