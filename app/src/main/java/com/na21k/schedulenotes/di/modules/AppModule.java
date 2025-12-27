package com.na21k.schedulenotes.di.modules;

import android.app.Application;
import android.content.Context;

import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.di.modules.repositories.CategoryRepositoriesModule;
import com.na21k.schedulenotes.di.modules.repositories.NoteRepositoriesModule;
import com.na21k.schedulenotes.di.modules.repositories.ScheduleRepositoriesModule;
import com.na21k.schedulenotes.di.modules.repositories.lists.ListRepositoriesModule;
import com.na21k.schedulenotes.repositories.Repository;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListItemsRepository;

import dagger.Binds;
import dagger.Module;

@Module(includes = {
        ScheduleRepositoriesModule.class,
        NoteRepositoriesModule.class,
        CategoryRepositoriesModule.class,
        ListRepositoriesModule.class
})
public interface AppModule {

    @Binds
    Context applicationContext(Application application);

    @Binds
    Repository<UserDefinedListItem> bindUserDefinedListItemsRepository(UserDefinedListItemsRepository userDefinedListItemsRepository);
}
