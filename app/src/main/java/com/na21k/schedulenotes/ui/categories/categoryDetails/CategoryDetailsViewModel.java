package com.na21k.schedulenotes.ui.categories.categoryDetails;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.models.ColorSetModel;
import com.na21k.schedulenotes.di.modules.CategoriesModule;
import com.na21k.schedulenotes.helpers.CategoriesHelper;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class CategoryDetailsViewModel extends ViewModel {

    @NonNull
    private final CategoriesRepository mCategoriesRepository;
    @Nullable
    private LiveData<Category> mCategory;
    private int mCategoryId;
    @NonNull
    private final List<ColorSetModel> mColorSetModels;

    private CategoryDetailsViewModel(
            @NonNull CategoriesRepository categoriesRepository,
            @NonNull List<ColorSetModel> colorSetModels
    ) {
        super();

        mCategoriesRepository = categoriesRepository;
        mColorSetModels = colorSetModels;
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

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final CategoriesRepository mCategoriesRepository;
        @NonNull
        private final List<ColorSetModel> mColorSetModels;

        @Inject
        public Factory(
                @NonNull CategoriesRepository categoriesRepository,
                @NonNull @CategoriesModule.CategoriesColorSets List<ColorSetModel> colorSetModels
        ) {
            mCategoriesRepository = categoriesRepository;
            mColorSetModels = colorSetModels;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            CategoryDetailsViewModel vm = new CategoryDetailsViewModel(
                    mCategoriesRepository, mColorSetModels
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
