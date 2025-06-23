package com.na21k.schedulenotes.ui.settings.importExport;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

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
import com.na21k.schedulenotes.repositories.Repository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListAttachedImagesRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class ExportActivityViewModel extends ViewModel {

    @NonNull
    private final Repository<Event> mScheduleRepository;
    @NonNull
    private final Repository<Note> mNotesRepository;
    @NonNull
    private final Repository<Category> mCategoriesRepository;
    @NonNull
    private final Repository<UserDefinedList> mUserDefinedListsRepository;
    @NonNull
    private final Repository<UserDefinedListItem> mUserDefinedListItemsRepository;
    @NonNull
    private final Repository<MoviesListItem> mMoviesListRepository;
    @NonNull
    private final Repository<MusicListItem> mMusicListRepository;
    @NonNull
    private final Repository<ShoppingListItem> mShoppingListRepository;
    @NonNull
    private final Repository<LanguagesListItem> mLanguagesListRepository;
    @NonNull
    private final LanguagesListAttachedImagesRepository mLanguagesListAttachedImagesRepository;

    private ExportActivityViewModel(
            @NonNull Repository<Event> scheduleRepository,
            @NonNull Repository<Note> notesRepository,
            @NonNull Repository<Category> categoriesRepository,
            @NonNull Repository<UserDefinedList> userDefinedListsRepository,
            @NonNull Repository<UserDefinedListItem> userDefinedListItemsRepository,
            @NonNull Repository<MoviesListItem> moviesListRepository,
            @NonNull Repository<MusicListItem> musicListRepository,
            @NonNull Repository<ShoppingListItem> shoppingListRepository,
            @NonNull Repository<LanguagesListItem> languagesListRepository,
            @NonNull LanguagesListAttachedImagesRepository languagesListAttachedImagesRepository
    ) {
        super();

        mScheduleRepository = scheduleRepository;
        mNotesRepository = notesRepository;
        mCategoriesRepository = categoriesRepository;
        mUserDefinedListsRepository = userDefinedListsRepository;
        mUserDefinedListItemsRepository = userDefinedListItemsRepository;
        mMoviesListRepository = moviesListRepository;
        mMusicListRepository = musicListRepository;
        mShoppingListRepository = shoppingListRepository;
        mLanguagesListRepository = languagesListRepository;
        mLanguagesListAttachedImagesRepository = languagesListAttachedImagesRepository;
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

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final Repository<Event> mScheduleRepository;
        @NonNull
        private final Repository<Note> mNotesRepository;
        @NonNull
        private final Repository<Category> mCategoriesRepository;
        @NonNull
        private final Repository<UserDefinedList> mUserDefinedListsRepository;
        @NonNull
        private final Repository<UserDefinedListItem> mUserDefinedListItemsRepository;
        @NonNull
        private final Repository<MoviesListItem> mMoviesListRepository;
        @NonNull
        private final Repository<MusicListItem> mMusicListRepository;
        @NonNull
        private final Repository<ShoppingListItem> mShoppingListRepository;
        @NonNull
        private final Repository<LanguagesListItem> mLanguagesListRepository;
        @NonNull
        private final LanguagesListAttachedImagesRepository mLanguagesListAttachedImagesRepository;

        @Inject
        public Factory(
                @NonNull Repository<Event> scheduleRepository,
                @NonNull Repository<Note> notesRepository,
                @NonNull Repository<Category> categoriesRepository,
                @NonNull Repository<UserDefinedList> userDefinedListsRepository,
                @NonNull Repository<UserDefinedListItem> userDefinedListItemsRepository,
                @NonNull Repository<MoviesListItem> moviesListRepository,
                @NonNull Repository<MusicListItem> musicListRepository,
                @NonNull Repository<ShoppingListItem> shoppingListRepository,
                @NonNull Repository<LanguagesListItem> languagesListRepository,
                @NonNull LanguagesListAttachedImagesRepository languagesListAttachedImagesRepository
        ) {
            mScheduleRepository = scheduleRepository;
            mNotesRepository = notesRepository;
            mCategoriesRepository = categoriesRepository;
            mUserDefinedListsRepository = userDefinedListsRepository;
            mUserDefinedListItemsRepository = userDefinedListItemsRepository;
            mMoviesListRepository = moviesListRepository;
            mMusicListRepository = musicListRepository;
            mShoppingListRepository = shoppingListRepository;
            mLanguagesListRepository = languagesListRepository;
            mLanguagesListAttachedImagesRepository = languagesListAttachedImagesRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            ExportActivityViewModel vm = new ExportActivityViewModel(
                    mScheduleRepository, mNotesRepository, mCategoriesRepository,
                    mUserDefinedListsRepository, mUserDefinedListItemsRepository,
                    mMoviesListRepository, mMusicListRepository, mShoppingListRepository,
                    mLanguagesListRepository, mLanguagesListAttachedImagesRepository
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
