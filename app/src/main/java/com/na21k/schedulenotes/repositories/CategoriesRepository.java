package com.na21k.schedulenotes.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Categories.CategoryDao;

import java.util.List;

public class CategoriesRepository extends MutableRepository<Category>
        implements CanSearchRepository<Category>, CanClearRepository {

    private final CategoryDao mCategoryDao = db.categoryDao();

    public CategoriesRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<List<Category>> getSearch(String query) {
        return mCategoryDao.search(query);
    }

    @Override
    public void clearBlocking() {
        mCategoryDao.deleteAll();
    }

    @Override
    protected BaseDao<Category> getDao() {
        return mCategoryDao;
    }
}
