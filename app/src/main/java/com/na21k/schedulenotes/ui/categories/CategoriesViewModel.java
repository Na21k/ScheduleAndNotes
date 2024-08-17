package com.na21k.schedulenotes.ui.categories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.repositories.CategoriesRepository;

import java.util.List;

public class CategoriesViewModel extends AndroidViewModel {

    private final CategoriesRepository mCategoriesRepository;

    public CategoriesViewModel(@NonNull Application application) {
        super(application);

        mCategoriesRepository = new CategoriesRepository(application);
    }

    public LiveData<List<Category>> getAll() {
        return mCategoriesRepository.getAll();
    }

    public LiveData<List<Category>> getCategoriesSearch(String searchQuery) {
        return mCategoriesRepository.getSearch(searchQuery);
    }

    public void delete(Category category) {
        mCategoriesRepository.delete(category);
    }
}
