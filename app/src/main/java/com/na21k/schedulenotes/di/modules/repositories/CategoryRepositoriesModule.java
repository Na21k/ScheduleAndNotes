package com.na21k.schedulenotes.di.modules.repositories;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.Repository;

import dagger.Binds;
import dagger.Module;

@Module
public interface CategoryRepositoriesModule {

    @Binds
    Repository<Category> bindCategoriesRepository(CategoriesRepository categoriesRepository);

    @Binds
    MutableRepository<Category> bindCategoriesRepositoryMutable(CategoriesRepository categoriesRepository);

    @Binds
    CanSearchRepository<Category> bindCanSearchCategoriesRepository(CategoriesRepository categoriesRepository);

    @Binds
    CanClearRepository<Category> bindCanClearCategoriesRepository(CategoriesRepository categoriesRepository);
}
