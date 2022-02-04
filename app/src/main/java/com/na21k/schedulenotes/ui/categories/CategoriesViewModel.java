package com.na21k.schedulenotes.ui.categories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Categories.CategoryDao;

import java.util.List;

public class CategoriesViewModel extends AndroidViewModel {

    private final CategoryDao mCategoryDao;
    private final LiveData<List<Category>> mAllCategories;

    public CategoriesViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mCategoryDao = db.categoryDao();
        mAllCategories = mCategoryDao.getAll();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveData<List<Category>> getAll() {
        return mAllCategories;
    }

    /*public LiveData<Category> getById(int id) {
        return mCategoryDao.getById(id);
    }*/

    public LiveData<List<Category>> search(String search) {
        return mCategoryDao.search(search);
    }

    public void delete(Category category) {
        new Thread(() -> mCategoryDao.delete(category)).start();
    }

    /*public void deleteAll() {
        new Thread(mCategoryDao::deleteAll).start();
    }*/
}
