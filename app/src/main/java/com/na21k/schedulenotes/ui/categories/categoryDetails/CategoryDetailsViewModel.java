package com.na21k.schedulenotes.ui.categories.categoryDetails;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.models.ColorSetModel;
import com.na21k.schedulenotes.di.modules.CategoriesModule;
import com.na21k.schedulenotes.helpers.CategoriesHelper;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

public class CategoryDetailsViewModel extends ViewModel {

    @NonNull
    private final MutableRepository<Category> mCategoriesRepository;
    private final int mCategoryId;
    @NonNull
    private final LiveData<Category> mCategory;
    @NonNull
    private final List<ColorSetModel> mColorSetModels;

    private CategoryDetailsViewModel(
            @NonNull MutableRepository<Category> categoriesRepository,
            int categoryId,
            @NonNull List<ColorSetModel> colorSetModels
    ) {
        super();

        mCategoriesRepository = categoriesRepository;
        mCategoryId = categoryId;
        mColorSetModels = colorSetModels;

        mCategory = categoriesRepository.getById(categoryId);
    }

    public boolean isEditing() {
        return mCategoryId != 0;
    }

    @NonNull
    public LiveData<Category> getCategory() {
        return mCategory;
    }

    public void saveCategory(@NonNull Category category) {
        category.setId(mCategoryId);

        if (isEditing()) mCategoriesRepository.update(category);
        else mCategoriesRepository.add(category);
    }

    public void deleteCategory() {
        mCategoriesRepository.delete(mCategoryId);
    }

    @NonNull
    public List<ColorSetModel> getColorSetModels() {
        return mColorSetModels;
    }

    @NonNull
    public ColorSetModel getDefaultColorSetModel() {
        return CategoriesHelper.getDefaultColorSetModel(mColorSetModels);
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final MutableRepository<Category> mCategoriesRepository;
        private final int mCategoryId;
        @NonNull
        private final List<ColorSetModel> mColorSetModels;

        @AssistedInject
        public Factory(
                @NonNull MutableRepository<Category> categoriesRepository,
                @Assisted int categoryId,
                @NonNull @CategoriesModule.CategoriesColorSets List<ColorSetModel> colorSetModels
        ) {
            mCategoriesRepository = categoriesRepository;
            mCategoryId = categoryId;
            mColorSetModels = colorSetModels;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            CategoryDetailsViewModel vm = new CategoryDetailsViewModel(
                    mCategoriesRepository, mCategoryId, mColorSetModels
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }

        @dagger.assisted.AssistedFactory
        public interface AssistedFactory {

            Factory create(int categoryId);
        }
    }
}
