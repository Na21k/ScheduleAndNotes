package com.na21k.schedulenotes.ui.categories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class CategoriesViewModel extends ViewModel {

    @NonNull
    private final CategoriesRepository mCategoriesRepository;

    private CategoriesViewModel(@NonNull CategoriesRepository categoriesRepository) {
        super();

        mCategoriesRepository = categoriesRepository;
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

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final CategoriesRepository mCategoriesRepository;

        @Inject
        public Factory(@NonNull CategoriesRepository categoriesRepository) {
            mCategoriesRepository = categoriesRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            CategoriesViewModel vm = new CategoriesViewModel(mCategoriesRepository);
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
