package com.na21k.schedulenotes.di.modules.repositories.lists.userDefinedListsRepositories;

import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.Repository;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListsRepository;

import dagger.Binds;
import dagger.Module;

@Module(includes = UserDefinedListItemsRepositoriesModule.class)
public interface UserDefinedListsRepositoriesModule {

    @Binds
    Repository<UserDefinedList> bindUserDefinedListsRepository(UserDefinedListsRepository userDefinedListsRepository);

    @Binds
    MutableRepository<UserDefinedList> bindUserDefinedListsRepositoryMutable(UserDefinedListsRepository userDefinedListsRepository);

    @Binds
    CanSearchRepository<UserDefinedList> bindCanSearchUserDefinedListsRepository(UserDefinedListsRepository userDefinedListsRepository);

    @Binds
    CanClearRepository<UserDefinedList> bindCanClearUserDefinedListsRepository(UserDefinedListsRepository userDefinedListsRepository);
}
