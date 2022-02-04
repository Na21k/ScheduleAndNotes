package com.na21k.schedulenotes.ui.categories.categoryDetails;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Categories.CategoryDao;

public class CategoryDetailsViewModel extends AndroidViewModel {

    private final CategoryDao mCategoryDao;
    private LiveData<Category> mCategory;
    private int mCategoryId;

    public CategoryDetailsViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mCategoryDao = db.categoryDao();
    }

    public LiveData<Category> getCategory(int id) {
        if (mCategoryId != id) {
            mCategory = mCategoryDao.getById(id);
            mCategoryId = id;
        }

        return mCategory;
    }

    public void createCategory(Category category) {
        new Thread(() -> mCategoryDao.insert(category)).start();
    }

    public void deleteCurrentCategory() {
        new Thread(() -> mCategoryDao.delete(mCategoryId)).start();
    }

    public void updateCurrentCategory(Category category) {
        category.setId(mCategoryId);
        new Thread(() -> mCategoryDao.update(category)).start();
    }

    @Nullable
    public LiveData<Category> getCurrentCategory() {
        return mCategory;
    }
}
