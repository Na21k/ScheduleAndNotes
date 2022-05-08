package com.na21k.schedulenotes.ui.lists.shopping;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItemDao;

import java.util.List;

public class ShoppingListViewModel extends AndroidViewModel {

    private final ShoppingListItemDao mShoppingListItemDao;
    private final LiveData<List<ShoppingListItem>> mAllItems;

    public ShoppingListViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mShoppingListItemDao = db.shoppingListItemDao();
        mAllItems = mShoppingListItemDao.getAll();
    }

    public LiveData<List<ShoppingListItem>> getAll() {
        return mAllItems;
    }

    public LiveData<List<ShoppingListItem>> getItemsSearch(String searchQuery) {
        return mShoppingListItemDao.search(searchQuery);
    }

    public void addNew(ShoppingListItem item) {
        new Thread(() -> mShoppingListItemDao.insert(item)).start();
    }

    public void update(ShoppingListItem item) {
        new Thread(() -> mShoppingListItemDao.update(item)).start();
    }

    public void delete(ShoppingListItem item) {
        new Thread(() -> mShoppingListItemDao.delete(item)).start();
    }

    public void deleteAll() {
        new Thread(mShoppingListItemDao::deleteAll).start();
    }
}
