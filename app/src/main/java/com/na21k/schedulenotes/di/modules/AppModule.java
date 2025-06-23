package com.na21k.schedulenotes.di.modules;

import android.app.Application;
import android.content.Context;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.repositories.NotesRepository;
import com.na21k.schedulenotes.repositories.Repository;
import com.na21k.schedulenotes.repositories.ScheduleRepository;
import com.na21k.schedulenotes.repositories.lists.MoviesListRepository;
import com.na21k.schedulenotes.repositories.lists.MusicListRepository;
import com.na21k.schedulenotes.repositories.lists.ShoppingListRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListRepository;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListItemsRepository;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListsRepository;

import dagger.Binds;
import dagger.Module;

@Module
public interface AppModule {

    @Binds
    Context applicationContext(Application application);

    @Binds
    Repository<Event> bindEentsRepository(ScheduleRepository scheduleRepository);

    @Binds
    Repository<Note> bindNotesRepository(NotesRepository notesRepository);

    @Binds
    Repository<Category> bindCategoriesRepository(CategoriesRepository categoriesRepository);

    @Binds
    Repository<UserDefinedList> bindUserDefinedListsRepository(UserDefinedListsRepository userDefinedListsRepository);

    @Binds
    Repository<UserDefinedListItem> bindUserDefinedListItemsRepository(UserDefinedListItemsRepository userDefinedListItemsRepository);

    @Binds
    Repository<MoviesListItem> bindMoviesListItemsRepository(MoviesListRepository moviesListRepository);

    @Binds
    Repository<MusicListItem> bindMusicListItemsRepository(MusicListRepository musicListRepository);

    @Binds
    Repository<ShoppingListItem> bindShoppingListItemsRepository(ShoppingListRepository shoppingListRepository);

    @Binds
    Repository<LanguagesListItem> bindLanguagesListItemsRepository(LanguagesListRepository languagesListRepository);
}
