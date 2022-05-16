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

import java.util.List;

public class ExportActivityViewModel extends AndroidViewModel {

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

    public ExportActivityViewModel(@NonNull Application application) {
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

    public List<Event> getAllEventsBlocking() {
        return mEventDao.getAllBlocking();
    }

    public List<Note> getAllNotesBlocking() {
        return mNoteDao.getAllBlocking();
    }

    public List<Category> getAllCategoriesBlocking() {
        return mCategoryDao.getAllBlocking();
    }

    public List<UserDefinedList> getAllUserDefinedListsBlocking() {
        return mUserDefinedListDao.getAllBlocking();
    }

    public List<UserDefinedListItem> getAllUserDefinedListItemsBlocking() {
        return mUserDefinedListItemDao.getAllBlocking();
    }

    public List<MoviesListItem> getAllMoviesListItemsBlocking() {
        return mMoviesListItemDao.getAllBlocking();
    }

    public List<MusicListItem> getAllMusicListItemsBlocking() {
        return mMusicListItemDao.getAllBlocking();
    }

    public List<ShoppingListItem> getAllShoppingListItemsBlocking() {
        return mShoppingListItemDao.getAllBlocking();
    }

    public List<LanguagesListItem> getAllLanguagesListItemsBlocking() {
        return mLanguagesListItemDao.getAllBlocking();
    }

    public List<LanguagesListItemAttachedImage>
    getAllLanguagesListItemAttachedImagesByListItemIdBlocking(int itemId) {
        return mLanguagesListItemAttachedImageDao.getByListItemIdBlocking(itemId);
    }
}
