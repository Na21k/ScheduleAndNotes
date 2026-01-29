package com.na21k.schedulenotes.ui.categories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class CategoriesViewModel extends ViewModel {

    @NonNull
    private final MutableRepository<Category> mMutableCategoriesRepository;
    @NonNull
    private final CanSearchRepository<Category> mCanSearchCategoriesRepository;

    private CategoriesViewModel(
            @NonNull MutableRepository<Category> mutableCategoriesRepository,
            @NonNull CanSearchRepository<Category> canSearchCategoriesRepository
    ) {
        super();

        mMutableCategoriesRepository = mutableCategoriesRepository;
        mCanSearchCategoriesRepository = canSearchCategoriesRepository;
    }

    public LiveData<List<Category>> getAll() {
        return mMutableCategoriesRepository.getAll();
    }

    public LiveData<List<Category>> getCategoriesSearch(String searchQuery) {
        return mCanSearchCategoriesRepository.getSearch(searchQuery);
    }

    public void delete(Category category) {
        mMutableCategoriesRepository.delete(category);
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final MutableRepository<Category> mMutableCategoriesRepository;
        @NonNull
        private final CanSearchRepository<Category> mCanSearchCategoriesRepository;

        @Inject
        public Factory(
                @NonNull MutableRepository<Category> mutableCategoriesRepository,
                @NonNull CanSearchRepository<Category> canSearchCategoriesRepository
        ) {
            mMutableCategoriesRepository = mutableCategoriesRepository;
            mCanSearchCategoriesRepository = canSearchCategoriesRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            CategoriesViewModel vm = new CategoriesViewModel(
                    mMutableCategoriesRepository, mCanSearchCategoriesRepository
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
