package com.na21k.schedulenotes.di.modules.repositories.lists;

import com.na21k.schedulenotes.di.modules.repositories.lists.languagesListRepositoriesModules.LanguagesListRepositoriesModule;
import com.na21k.schedulenotes.di.modules.repositories.lists.userDefinedListsRepositories.UserDefinedListsRepositoriesModule;

import dagger.Module;

@Module(includes = {
        MoviesListRepositoriesModule.class,
        MusicListRepositoriesModule.class,
        ShoppingListRepositoriesModule.class,
        LanguagesListRepositoriesModule.class,
        UserDefinedListsRepositoriesModule.class
})
public interface ListRepositoriesModule {
}
