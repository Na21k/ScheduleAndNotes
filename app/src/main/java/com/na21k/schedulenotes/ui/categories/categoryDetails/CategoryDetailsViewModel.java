package com.na21k.schedulenotes.ui.categories.categoryDetails;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.models.ColorSetModel;
import com.na21k.schedulenotes.helpers.CategoriesHelper;
import com.na21k.schedulenotes.repositories.CategoriesRepository;

import java.util.List;

public class CategoryDetailsViewModel extends AndroidViewModel {

    private final CategoriesRepository mCategoriesRepository;
    private LiveData<Category> mCategory;
    private int mCategoryId;
    private final List<ColorSetModel> mColorSetModels;

    public CategoryDetailsViewModel(@NonNull Application application) {
        super(application);

        mCategoriesRepository = new CategoriesRepository(application);
        mColorSetModels = CategoriesHelper.getCategoriesColorSets(application);
    }

    public LiveData<Category> getCategory(int id) {
        if (mCategoryId != id) {
            mCategory = mCategoriesRepository.getById(id);
            mCategoryId = id;
        }

        return mCategory;
    }

    public void createCategory(Category category) {
        mCategoriesRepository.add(category);
    }

    public void deleteCurrentCategory() {
        mCategoriesRepository.delete(mCategoryId);
    }

    public void updateCurrentCategory(@NonNull Category category) {
        category.setId(mCategoryId);
        mCategoriesRepository.update(category);
    }

    @Nullable
    public LiveData<Category> getCurrentCategory() {
        return mCategory;
    }

    @NonNull
    public List<ColorSetModel> getColorSetModels() {
        return mColorSetModels;
    }

    public ColorSetModel getDefaultColorSetModel() {
        return CategoriesHelper.getDefaultColorSetModel(mColorSetModels);
    }
}
