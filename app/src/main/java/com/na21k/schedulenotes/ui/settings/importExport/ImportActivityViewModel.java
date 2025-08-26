package com.na21k.schedulenotes.ui.settings.importExport;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
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
import com.na21k.schedulenotes.helpers.AlarmsHelper;
import com.na21k.schedulenotes.helpers.EventsHelper2;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.NotesRepository;
import com.na21k.schedulenotes.repositories.lists.MoviesListRepository;
import com.na21k.schedulenotes.repositories.lists.MusicListRepository;
import com.na21k.schedulenotes.repositories.lists.ShoppingListRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListAttachedImagesRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListRepository;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListItemsRepository;
import com.na21k.schedulenotes.repositories.lists.userDefined.UserDefinedListsRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class ImportActivityViewModel extends AndroidViewModel {

    @NonNull
    private final MutableRepository<Event> mMutableScheduleRepository;
    @NonNull
    private final CanClearRepository<Event> mCanClearScheduleRepository;
    @NonNull
    private final NotesRepository mNotesRepository;
    @NonNull
    private final CategoriesRepository mCategoriesRepository;
    @NonNull
    private final UserDefinedListsRepository mUserDefinedListsRepository;
    @NonNull
    private final UserDefinedListItemsRepository mUserDefinedListItemsRepository;
    @NonNull
    private final MoviesListRepository mMoviesListRepository;
    @NonNull
    private final MusicListRepository mMusicListRepository;
    @NonNull
    private final ShoppingListRepository mShoppingListRepository;
    @NonNull
    private final LanguagesListRepository mLanguagesListRepository;
    @NonNull
    private final LanguagesListAttachedImagesRepository mLanguagesListAttachedImagesRepository;
    @NonNull
    private final AlarmsHelper mAlarmsHelper;
    @NonNull
    private final EventsHelper2 mEventsHelper;

    private ImportActivityViewModel(
            @NonNull Application application,
            @NonNull MutableRepository<Event> mutableScheduleRepository,
            @NonNull CanClearRepository<Event> canClearScheduleRepository,
            @NonNull NotesRepository notesRepository,
            @NonNull CategoriesRepository categoriesRepository,
            @NonNull UserDefinedListsRepository userDefinedListsRepository,
            @NonNull UserDefinedListItemsRepository userDefinedListItemsRepository,
            @NonNull MoviesListRepository moviesListRepository,
            @NonNull MusicListRepository musicListRepository,
            @NonNull ShoppingListRepository shoppingListRepository,
            @NonNull LanguagesListRepository languagesListRepository,
            @NonNull LanguagesListAttachedImagesRepository languagesListAttachedImagesRepository,
            @NonNull AlarmsHelper alarmsHelper,
            @NonNull EventsHelper2 eventsHelper
    ) {
        super(application);

        mMutableScheduleRepository = mutableScheduleRepository;
        mCanClearScheduleRepository = canClearScheduleRepository;
        mNotesRepository = notesRepository;
        mCategoriesRepository = categoriesRepository;
        mUserDefinedListsRepository = userDefinedListsRepository;
        mUserDefinedListItemsRepository = userDefinedListItemsRepository;
        mMoviesListRepository = moviesListRepository;
        mMusicListRepository = musicListRepository;
        mShoppingListRepository = shoppingListRepository;
        mLanguagesListRepository = languagesListRepository;
        mLanguagesListAttachedImagesRepository = languagesListAttachedImagesRepository;
        mAlarmsHelper = alarmsHelper;
        mEventsHelper = eventsHelper;
    }

    public void cancelAllEventNotificationAlarmsAndClearDatabaseBlocking() {
        mAlarmsHelper.cancelAllEventNotificationAlarmsBlocking();

        mCanClearScheduleRepository.clearBlocking();
        mNotesRepository.clearBlocking();
        mCategoriesRepository.clearBlocking();
        mUserDefinedListsRepository.clearBlocking();
        mMoviesListRepository.clearBlocking();
        mMusicListRepository.clearBlocking();
        mShoppingListRepository.clearBlocking();
        mLanguagesListRepository.clearBlocking();
    }

    public void insertEventsBlocking(List<Event> events) {
        mMutableScheduleRepository.addBlocking(events);
        Date now = new Date();

        for (Event event : events) {
            if (event.getDateTimeStarts().before(now)) {
                continue;
            }

            mEventsHelper.scheduleEventNotificationsBlocking(event);
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

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final Application mApplication;
        @NonNull
        private final MutableRepository<Event> mMutableScheduleRepository;
        @NonNull
        private final CanClearRepository<Event> mCanClearScheduleRepository;
        @NonNull
        private final NotesRepository mNotesRepository;
        @NonNull
        private final CategoriesRepository mCategoriesRepository;
        @NonNull
        private final UserDefinedListsRepository mUserDefinedListsRepository;
        @NonNull
        private final UserDefinedListItemsRepository mUserDefinedListItemsRepository;
        @NonNull
        private final MoviesListRepository mMoviesListRepository;
        @NonNull
        private final MusicListRepository mMusicListRepository;
        @NonNull
        private final ShoppingListRepository mShoppingListRepository;
        @NonNull
        private final LanguagesListRepository mLanguagesListRepository;
        @NonNull
        private final LanguagesListAttachedImagesRepository mLanguagesListAttachedImagesRepository;
        @NonNull
        private final AlarmsHelper mAlarmsHelper;
        @NonNull
        private final EventsHelper2 mEventsHelper;

        @Inject
        public Factory(
                @NonNull Application application,
                @NonNull MutableRepository<Event> mutableScheduleRepository,
                @NonNull CanClearRepository<Event> canClearScheduleRepository,
                @NonNull NotesRepository notesRepository,
                @NonNull CategoriesRepository categoriesRepository,
                @NonNull UserDefinedListsRepository userDefinedListsRepository,
                @NonNull UserDefinedListItemsRepository userDefinedListItemsRepository,
                @NonNull MoviesListRepository moviesListRepository,
                @NonNull MusicListRepository musicListRepository,
                @NonNull ShoppingListRepository shoppingListRepository,
                @NonNull LanguagesListRepository languagesListRepository,
                @NonNull LanguagesListAttachedImagesRepository languagesListAttachedImagesRepository,
                @NonNull AlarmsHelper alarmsHelper,
                @NonNull EventsHelper2 eventsHelper
        ) {
            mApplication = application;
            mMutableScheduleRepository = mutableScheduleRepository;
            mCanClearScheduleRepository = canClearScheduleRepository;
            mNotesRepository = notesRepository;
            mCategoriesRepository = categoriesRepository;
            mUserDefinedListsRepository = userDefinedListsRepository;
            mUserDefinedListItemsRepository = userDefinedListItemsRepository;
            mMoviesListRepository = moviesListRepository;
            mMusicListRepository = musicListRepository;
            mShoppingListRepository = shoppingListRepository;
            mLanguagesListRepository = languagesListRepository;
            mLanguagesListAttachedImagesRepository = languagesListAttachedImagesRepository;
            mAlarmsHelper = alarmsHelper;
            mEventsHelper = eventsHelper;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            ImportActivityViewModel vm = new ImportActivityViewModel(
                    mApplication,
                    mMutableScheduleRepository, mCanClearScheduleRepository,
                    mNotesRepository, mCategoriesRepository,
                    mUserDefinedListsRepository, mUserDefinedListItemsRepository,
                    mMoviesListRepository, mMusicListRepository, mShoppingListRepository,
                    mLanguagesListRepository, mLanguagesListAttachedImagesRepository,
                    mAlarmsHelper, mEventsHelper
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
