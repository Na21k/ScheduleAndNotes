package com.na21k.schedulenotes.ui.settings.importExport;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Categories.CategoryDao;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImageDao;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemDao;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItemDao;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItemDao;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItemDao;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListDao;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItemDao;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.data.database.Notes.NoteDao;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventDao;
import com.na21k.schedulenotes.helpers.EventsHelper;

import java.util.Date;
import java.util.List;

public class ImportActivityViewModel extends AndroidViewModel {

    private final EventDao mEventDao;
    private final NoteDao mNoteDao;
    private final CategoryDao mCategoryDao;
    private final UserDefinedListDao mUserDefinedListDao;
    private final UserDefinedListItemDao mUserDefinedListItemDao;
    private final MoviesListItemDao mMoviesListItemDao;
    private final MusicListItemDao mMusicListItemDao;
    private final ShoppingListItemDao mShoppingListItemDao;
    private final LanguagesListItemDao mLanguagesListItemDao;
    private final LanguagesListItemAttachedImageDao mLanguagesListItemAttachedImageDao;

    public ImportActivityViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        mEventDao = db.eventDao();
        mNoteDao = db.noteDao();
        mCategoryDao = db.categoryDao();
        mUserDefinedListDao = db.userDefinedListDao();
        mUserDefinedListItemDao = db.userDefinedListItemDao();
        mMoviesListItemDao = db.moviesListItemDao();
        mMusicListItemDao = db.musicListItemDao();
        mShoppingListItemDao = db.shoppingListItemDao();
        mLanguagesListItemDao = db.languagesListItemDao();
        mLanguagesListItemAttachedImageDao = db.languagesListItemAttachedImageDao();
    }

    public void clearDatabase() {
        mEventDao.deleteAll();
        mNoteDao.deleteAll();
        mCategoryDao.deleteAll();
        mUserDefinedListDao.deleteAll();
        mMoviesListItemDao.deleteAll();
        mMusicListItemDao.deleteAll();
        mShoppingListItemDao.deleteAll();
        mLanguagesListItemDao.deleteAll();
    }

    public void insertEventsBlocking(List<Event> events) {
        mEventDao.insert(events);
        Date now = new Date();

        for (Event event : events) {
            if (event.getDateTimeStarts().before(now)) {
                continue;
            }

            EventsHelper.scheduleEventNotificationsBlocking(event, getApplication());
        }
    }

    public void insertNotesBlocking(List<Note> notes) {
        mNoteDao.insert(notes);
    }

    public void insertCategoriesBlocking(List<Category> categories) {
        mCategoryDao.insert(categories);
    }

    public void insertUserDefinedListsBlocking(List<UserDefinedList> lists) {
        mUserDefinedListDao.insert(lists);
    }

    public void insertUserDefinedListItemsBlocking(List<UserDefinedListItem> items) {
        mUserDefinedListItemDao.insert(items);
    }

    public void insertMoviesListItemsBlocking(List<MoviesListItem> items) {
        mMoviesListItemDao.insert(items);
    }

    public void insertMusicListItemsBlocking(List<MusicListItem> items) {
        mMusicListItemDao.insert(items);
    }

    public void insertShoppingListItemsBlocking(List<ShoppingListItem> items) {
        mShoppingListItemDao.insert(items);
    }

    public void insertLanguagesListItemsBlocking(List<LanguagesListItem> items) {
        mLanguagesListItemDao.insert(items);
    }

    public void insertLanguageListItemAttachedImagesBlocking(
            List<LanguagesListItemAttachedImage> images) {
        mLanguagesListItemAttachedImageDao.insert(images);
    }
}
