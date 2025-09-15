package com.na21k.schedulenotes.di.modules;

import android.app.Application;
import android.content.Context;

import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.di.modules.repositories.CategoryRepositoriesModule;
import com.na21k.schedulenotes.di.modules.repositories.NoteRepositoriesModule;
import com.na21k.schedulenotes.di.modules.repositories.ScheduleRepositoriesModule;
import com.na21k.schedulenotes.di.modules.repositories.lists.ListRepositoriesModule;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.Repository;
import com.na21k.schedulenotes.repositories.lists.MusicListRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListRepository;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListItemsRepository;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListsRepository;

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
    Repository<UserDefinedList> bindUserDefinedListsRepository(UserDefinedListsRepository userDefinedListsRepository);

    @Binds
    Repository<UserDefinedListItem> bindUserDefinedListItemsRepository(UserDefinedListItemsRepository userDefinedListItemsRepository);

    @Binds
    Repository<MusicListItem> bindMusicListItemsRepository(MusicListRepository musicListRepository);

    @Binds
    Repository<LanguagesListItem> bindLanguagesListItemsRepository(LanguagesListRepository languagesListRepository);

    @Binds
    MutableRepository<LanguagesListItem> bindLanguagesListItemsRepositoryMutable(LanguagesListRepository languagesListRepository);
}
