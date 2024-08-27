package com.na21k.schedulenotes.ui.settings.importExport;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.helpers.EventsHelper;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.repositories.NotesRepository;
import com.na21k.schedulenotes.repositories.ScheduleRepository;
import com.na21k.schedulenotes.repositories.lists.MoviesListRepository;
import com.na21k.schedulenotes.repositories.lists.MusicListRepository;
import com.na21k.schedulenotes.repositories.lists.ShoppingListRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListAttachedImagesRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListRepository;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListItemsRepository;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListsRepository;

import java.util.Date;
import java.util.List;

public class ImportActivityViewModel extends AndroidViewModel {

    private final ScheduleRepository mScheduleRepository;
    private final NotesRepository mNotesRepository;
    private final CategoriesRepository mCategoriesRepository;
    private final UserDefinedListsRepository mUserDefinedListsRepository;
    private final UserDefinedListItemsRepository mUserDefinedListItemsRepository;
    private final MoviesListRepository mMoviesListRepository;
    private final MusicListRepository mMusicListRepository;
    private final ShoppingListRepository mShoppingListRepository;
    private final LanguagesListRepository mLanguagesListRepository;
    private final LanguagesListAttachedImagesRepository mLanguagesListAttachedImagesRepository;

    public ImportActivityViewModel(@NonNull Application application) {
        super(application);
        mScheduleRepository = new ScheduleRepository(application);
        mNotesRepository = new NotesRepository(application);
        mCategoriesRepository = new CategoriesRepository(application);
        mUserDefinedListsRepository = new UserDefinedListsRepository(application);
        mUserDefinedListItemsRepository = new UserDefinedListItemsRepository(application);
        mMoviesListRepository = new MoviesListRepository(application);
        mMusicListRepository = new MusicListRepository(application);
        mShoppingListRepository = new ShoppingListRepository(application);
        mLanguagesListRepository = new LanguagesListRepository(application);
        mLanguagesListAttachedImagesRepository = new LanguagesListAttachedImagesRepository(application);
    }

    public void clearDatabaseBlocking() {
        mScheduleRepository.clearBlocking();
        mNotesRepository.clearBlocking();
        mCategoriesRepository.clearBlocking();
        mUserDefinedListsRepository.clearBlocking();
        mMoviesListRepository.clearBlocking();
        mMusicListRepository.clearBlocking();
        mShoppingListRepository.clearBlocking();
        mLanguagesListRepository.clearBlocking();
    }

    public void insertEventsBlocking(List<Event> events) {
        mScheduleRepository.addBlocking(events);
        Date now = new Date();

        for (Event event : events) {
            if (event.getDateTimeStarts().before(now)) {
                continue;
            }

            EventsHelper.scheduleEventNotificationsBlocking(event, getApplication());
        }
    }

    public void insertNotesBlocking(List<Note> notes) {
        mNotesRepository.addBlocking(notes);
    }

    public void insertCategoriesBlocking(List<Category> categories) {
        mCategoriesRepository.addBlocking(categories);
    }

    public void insertUserDefinedListsBlocking(List<UserDefinedList> lists) {
        mUserDefinedListsRepository.addBlocking(lists);
    }

    public void insertUserDefinedListItemsBlocking(List<UserDefinedListItem> items) {
        mUserDefinedListItemsRepository.addBlocking(items);
    }

    public void insertMoviesListItemsBlocking(List<MoviesListItem> items) {
        mMoviesListRepository.addBlocking(items);
    }

    public void insertMusicListItemsBlocking(List<MusicListItem> items) {
        mMusicListRepository.addBlocking(items);
    }

    public void insertShoppingListItemsBlocking(List<ShoppingListItem> items) {
        mShoppingListRepository.addBlocking(items);
    }

    public void insertLanguagesListItemsBlocking(List<LanguagesListItem> items) {
        mLanguagesListRepository.addBlocking(items);
    }

    public void insertLanguageListItemAttachedImagesBlocking(
            List<LanguagesListItemAttachedImage> images) {
        mLanguagesListAttachedImagesRepository.addBlocking(images);
    }
}
