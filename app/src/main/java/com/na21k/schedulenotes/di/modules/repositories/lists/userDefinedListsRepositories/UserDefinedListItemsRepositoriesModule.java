package com.na21k.schedulenotes.di.modules.repositories.lists.userDefinedListsRepositories;

import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.Repository;
import com.na21k.schedulenotes.repositories.lists.userDefined.listItems.UserDefinedListItemsRepository;
import com.na21k.schedulenotes.repositories.lists.userDefined.listItems.UserDefinedListItemsRepositoryImpl;

import dagger.Binds;
import dagger.Module;

@Module
public interface UserDefinedListItemsRepositoriesModule {

    @Binds
    Repository<UserDefinedListItem> bindUserDefinedListItemsItemsRepository(UserDefinedListItemsRepositoryImpl userDefinedListItemsRepository);

    @Binds
    MutableRepository<UserDefinedListItem> bindUserDefinedListItemsItemsRepositoryMutable(UserDefinedListItemsRepositoryImpl userDefinedListItemsRepository);

    @Binds
    UserDefinedListItemsRepository bindUserDefinedListItemsRepository(UserDefinedListItemsRepositoryImpl userDefinedListItemsRepository);
}
