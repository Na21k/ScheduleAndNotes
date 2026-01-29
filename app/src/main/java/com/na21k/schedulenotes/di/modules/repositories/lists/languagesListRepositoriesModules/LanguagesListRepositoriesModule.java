package com.na21k.schedulenotes.di.modules.repositories.lists.languagesListRepositoriesModules;

import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanProvideRandomRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.Repository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListRepositoryImpl;

import dagger.Binds;
import dagger.Module;

@Module(includes = LanguagesListAttachedImagesRepositoriesModule.class)
public interface LanguagesListRepositoriesModule {

    @Binds
    Repository<LanguagesListItem> bindLanguagesListItemsRepository(LanguagesListRepositoryImpl languagesListRepository);

    @Binds
    MutableRepository<LanguagesListItem> bindLanguagesListItemsRepositoryMutable(LanguagesListRepositoryImpl languagesListRepository);

    @Binds
    LanguagesListRepository bindLanguagesListRepository(LanguagesListRepositoryImpl languagesListRepository);

    @Binds
    CanSearchRepository<LanguagesListItem> bindCanSearchLanguagesListRepository(LanguagesListRepositoryImpl languagesListRepository);

    @Binds
    CanClearRepository<LanguagesListItem> bindCanClearLanguagesListRepository(LanguagesListRepositoryImpl languagesListRepository);

    @Binds
    CanProvideRandomRepository<LanguagesListItem> bindCanProvideRandomLanguagesListRepository(LanguagesListRepositoryImpl languagesListRepository);
}
