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
import com.na21k.schedulenotes.repositories.CanListRepository;
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

import java.util.List;

public class ExportActivityViewModel extends AndroidViewModel {

    private final CanListRepository<Event> mScheduleRepository;
    private final CanListRepository<Note> mNotesRepository;
    private final CanListRepository<Category> mCategoriesRepository;
    private final CanListRepository<UserDefinedList> mUserDefinedListsRepository;
    private final CanListRepository<UserDefinedListItem> mUserDefinedListItemsRepository;
    private final CanListRepository<MoviesListItem> mMoviesListRepository;
    private final CanListRepository<MusicListItem> mMusicListRepository;
    private final CanListRepository<ShoppingListItem> mShoppingListRepository;
    private final CanListRepository<LanguagesListItem> mLanguagesListRepository;
    private final LanguagesListAttachedImagesRepository mLanguagesListAttachedImagesRepository;

    public ExportActivityViewModel(@NonNull Application application) {
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

    public List<Event> getAllEventsBlocking() {
        return mScheduleRepository.getAllBlocking();
    }

    public List<Note> getAllNotesBlocking() {
        return mNotesRepository.getAllBlocking();
    }

    public List<Category> getAllCategoriesBlocking() {
        return mCategoriesRepository.getAllBlocking();
    }

    public List<UserDefinedList> getAllUserDefinedListsBlocking() {
        return mUserDefinedListsRepository.getAllBlocking();
    }

    public List<UserDefinedListItem> getAllUserDefinedListItemsBlocking() {
        return mUserDefinedListItemsRepository.getAllBlocking();
    }

    public List<MoviesListItem> getAllMoviesListItemsBlocking() {
        return mMoviesListRepository.getAllBlocking();
    }

    public List<MusicListItem> getAllMusicListItemsBlocking() {
        return mMusicListRepository.getAllBlocking();
    }

    public List<ShoppingListItem> getAllShoppingListItemsBlocking() {
        return mShoppingListRepository.getAllBlocking();
    }

    public List<LanguagesListItem> getAllLanguagesListItemsBlocking() {
        return mLanguagesListRepository.getAllBlocking();
    }

    public List<LanguagesListItemAttachedImage>
    getAllLanguagesListItemAttachedImagesByListItemIdBlocking(int itemId) {
        return mLanguagesListAttachedImagesRepository.getByListItemIdBlocking(itemId);
    }
}
