package com.na21k.schedulenotes.repositories.lists;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItemDao;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

public class ShoppingListRepository extends MutableRepository<ShoppingListItem>
        implements CanSearchRepository<ShoppingListItem>, CanClearRepository {

    private final ShoppingListItemDao mShoppingListItemDao = db.shoppingListItemDao();

    public ShoppingListRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<List<ShoppingListItem>> getSearch(String query) {
        return mShoppingListItemDao.search(query);
    }

    public void deleteChecked() {
        new Thread(mShoppingListItemDao::deleteChecked).start();
    }

    @Override
    public void clearBlocking() {
        mShoppingListItemDao.deleteAll();
    }

    @Override
    protected BaseDao<ShoppingListItem> getDao() {
        return mShoppingListItemDao;
    }
}
