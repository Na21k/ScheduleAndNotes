package com.na21k.schedulenotes.repositories.lists.shopping;

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

import javax.inject.Inject;

public class ShoppingListRepositoryImpl extends MutableRepository<ShoppingListItem>
        implements ShoppingListRepository,
        CanSearchRepository<ShoppingListItem>, CanClearRepository<ShoppingListItem> {

    private final ShoppingListItemDao mShoppingListItemDao = db.shoppingListItemDao();

    @Inject
    public ShoppingListRepositoryImpl(@NonNull Context context) {
        super(context);
    }

    @Override
    protected BaseDao<ShoppingListItem> getDao() {
        return mShoppingListItemDao;
    }

    @Override
    public void deleteChecked() {
        new Thread(mShoppingListItemDao::deleteChecked).start();
    }

    @Override
    public LiveData<List<ShoppingListItem>> getSearch(String query) {
        return mShoppingListItemDao.search(query);
    }

    @Override
    public void clearBlocking() {
        mShoppingListItemDao.deleteAll();
    }
}
