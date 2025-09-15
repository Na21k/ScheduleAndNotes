package com.na21k.schedulenotes.di.modules.repositories.lists;

import dagger.Module;

@Module(includes = {
        MoviesListRepositoriesModule.class,
        ShoppingListRepositoriesModule.class
})
public interface ListRepositoriesModule {
}
