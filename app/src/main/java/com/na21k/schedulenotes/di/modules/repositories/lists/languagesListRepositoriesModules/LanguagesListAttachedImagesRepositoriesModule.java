package com.na21k.schedulenotes.di.modules.repositories.lists.languagesListRepositoriesModules;

import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.Repository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListAttachedImagesRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListAttachedImagesRepositoryImpl;

import dagger.Binds;
import dagger.Module;

@Module
public interface LanguagesListAttachedImagesRepositoriesModule {

    @Binds
    Repository<LanguagesListItemAttachedImage> bindLanguagesListAttachedImageItemsRepository(LanguagesListAttachedImagesRepositoryImpl languagesListAttachedImagesRepository);

    @Binds
    MutableRepository<LanguagesListItemAttachedImage> bindLanguagesListAttachedImageItemsRepositoryMutable(LanguagesListAttachedImagesRepositoryImpl languagesListAttachedImagesRepository);

    @Binds
    LanguagesListAttachedImagesRepository bindLanguagesListAttachedImagesRepository(LanguagesListAttachedImagesRepositoryImpl languagesListAttachedImagesRepository);
}
