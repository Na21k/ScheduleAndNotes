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
import com.na21k.schedulenotes.helpers.EventsHelper;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
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
    private final MutableRepository<Note> mMutableNotesRepository;
    @NonNull
    private final CanClearRepository<Note> mCanClearNotesRepository;
    @NonNull
    private final MutableRepository<Category> mMutableCategoriesRepository;
    @NonNull
    private final CanClearRepository<Category> mCanClearCategoriesRepository;
    @NonNull
    private final MutableRepository<UserDefinedList> mMutableUserDefinedListsRepository;
    @NonNull
    private final CanClearRepository<UserDefinedList> mCanClearUserDefinedListsRepository;
    @NonNull
    private final MutableRepository<UserDefinedListItem> mMutableUserDefinedListItemsRepository;
    @NonNull
    private final MutableRepository<MoviesListItem> mMutableMoviesListRepository;
    @NonNull
    private final CanClearRepository<MoviesListItem> mCanClearMoviesListRepository;
    @NonNull
    private final MutableRepository<MusicListItem> mMutableMusicListRepository;
    @NonNull
    private final CanClearRepository<MusicListItem> mCanClearMusicListRepository;
    @NonNull
    private final MutableRepository<ShoppingListItem> mMutableShoppingListRepository;
    @NonNull
    private final CanClearRepository<ShoppingListItem> mCanClearShoppingListRepository;
    @NonNull
    private final MutableRepository<LanguagesListItem> mMutableLanguagesListRepository;
    @NonNull
    private final CanClearRepository<LanguagesListItem> mCanClearLanguagesListRepository;
    @NonNull
    private final MutableRepository<LanguagesListItemAttachedImage> mMutableLanguagesListAttachedImagesRepository;
    @NonNull
    private final AlarmsHelper mAlarmsHelper;
    @NonNull
    private final EventsHelper mEventsHelper;

    private ImportActivityViewModel(
            @NonNull Application application,
            @NonNull MutableRepository<Event> mutableScheduleRepository,
            @NonNull CanClearRepository<Event> canClearScheduleRepository,
            @NonNull MutableRepository<Note> mutableNotesRepository,
            @NonNull CanClearRepository<Note> canClearNotesRepository,
            @NonNull MutableRepository<Category> mutableCategoriesRepository,
            @NonNull CanClearRepository<Category> canClearCategoriesRepository,
            @NonNull MutableRepository<UserDefinedList> mutableUserDefinedListsRepository,
            @NonNull CanClearRepository<UserDefinedList> canClearUserDefinedListsRepository,
            @NonNull MutableRepository<UserDefinedListItem> mutableUserDefinedListItemsRepository,
            @NonNull MutableRepository<MoviesListItem> mutableMoviesListRepository,
            @NonNull CanClearRepository<MoviesListItem> canClearMoviesListRepository,
            @NonNull MutableRepository<MusicListItem> mutableMusicListRepository,
            @NonNull CanClearRepository<MusicListItem> canClearMusicListRepository,
            @NonNull MutableRepository<ShoppingListItem> mutableShoppingListRepository,
            @NonNull CanClearRepository<ShoppingListItem> canClearShoppingListRepository,
            @NonNull MutableRepository<LanguagesListItem> mutableLanguagesListRepository,
            @NonNull CanClearRepository<LanguagesListItem> canClearLanguagesListRepository,
            @NonNull MutableRepository<LanguagesListItemAttachedImage> mutableLanguagesListAttachedImagesRepository,
            @NonNull AlarmsHelper alarmsHelper,
            @NonNull EventsHelper eventsHelper
    ) {
        super(application);

        mMutableScheduleRepository = mutableScheduleRepository;
        mCanClearScheduleRepository = canClearScheduleRepository;
        mMutableNotesRepository = mutableNotesRepository;
        mCanClearNotesRepository = canClearNotesRepository;
        mMutableCategoriesRepository = mutableCategoriesRepository;
        mCanClearCategoriesRepository = canClearCategoriesRepository;
        mMutableUserDefinedListsRepository = mutableUserDefinedListsRepository;
        mCanClearUserDefinedListsRepository = canClearUserDefinedListsRepository;
        mMutableUserDefinedListItemsRepository = mutableUserDefinedListItemsRepository;
        mMutableMoviesListRepository = mutableMoviesListRepository;
        mCanClearMoviesListRepository = canClearMoviesListRepository;
        mMutableMusicListRepository = mutableMusicListRepository;
        mCanClearMusicListRepository = canClearMusicListRepository;
        mMutableShoppingListRepository = mutableShoppingListRepository;
        mCanClearShoppingListRepository = canClearShoppingListRepository;
        mMutableLanguagesListRepository = mutableLanguagesListRepository;
        mCanClearLanguagesListRepository = canClearLanguagesListRepository;
        mMutableLanguagesListAttachedImagesRepository = mutableLanguagesListAttachedImagesRepository;
        mAlarmsHelper = alarmsHelper;
        mEventsHelper = eventsHelper;
    }

    public void cancelAllEventNotificationAlarmsAndClearDatabaseBlocking() {
        mAlarmsHelper.cancelAllEventNotificationAlarmsBlocking();

        mCanClearScheduleRepository.clearBlocking();
        mCanClearNotesRepository.clearBlocking();
        mCanClearCategoriesRepository.clearBlocking();
        mCanClearUserDefinedListsRepository.clearBlocking();
        mCanClearMoviesListRepository.clearBlocking();
        mCanClearMusicListRepository.clearBlocking();
        mCanClearShoppingListRepository.clearBlocking();
        mCanClearLanguagesListRepository.clearBlocking();
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
        mMutableNotesRepository.addBlocking(notes);
    }

    public void insertCategoriesBlocking(List<Category> categories) {
        mMutableCategoriesRepository.addBlocking(categories);
    }

    public void insertUserDefinedListsBlocking(List<UserDefinedList> lists) {
        mMutableUserDefinedListsRepository.addBlocking(lists);
    }

    public void insertUserDefinedListItemsBlocking(List<UserDefinedListItem> items) {
        mMutableUserDefinedListItemsRepository.addBlocking(items);
    }

    public void insertMoviesListItemsBlocking(List<MoviesListItem> items) {
        mMutableMoviesListRepository.addBlocking(items);
    }

    public void insertMusicListItemsBlocking(List<MusicListItem> items) {
        mMutableMusicListRepository.addBlocking(items);
    }

    public void insertShoppingListItemsBlocking(List<ShoppingListItem> items) {
        mMutableShoppingListRepository.addBlocking(items);
    }

    public void insertLanguagesListItemsBlocking(List<LanguagesListItem> items) {
        mMutableLanguagesListRepository.addBlocking(items);
    }

    public void insertLanguageListItemAttachedImagesBlocking(
            List<LanguagesListItemAttachedImage> images) {
        mMutableLanguagesListAttachedImagesRepository.addBlocking(images);
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final Application mApplication;
        @NonNull
        private final MutableRepository<Event> mMutableScheduleRepository;
        @NonNull
        private final CanClearRepository<Event> mCanClearScheduleRepository;
        @NonNull
        private final MutableRepository<Note> mMutableNotesRepository;
        @NonNull
        private final CanClearRepository<Note> mCanClearNotesRepository;
        @NonNull
        private final MutableRepository<Category> mMutableCategoriesRepository;
        @NonNull
        private final CanClearRepository<Category> mCanClearCategoriesRepository;
        @NonNull
        private final MutableRepository<UserDefinedList> mMutableUserDefinedListsRepository;
        @NonNull
        private final CanClearRepository<UserDefinedList> mCanClearUserDefinedListsRepository;
        @NonNull
        private final MutableRepository<UserDefinedListItem> mMutableUserDefinedListItemsRepository;
        @NonNull
        private final MutableRepository<MoviesListItem> mMutableMoviesListRepository;
        @NonNull
        private final CanClearRepository<MoviesListItem> mCanClearMoviesListRepository;
        @NonNull
        private final MutableRepository<MusicListItem> mMutableMusicListRepository;
        @NonNull
        private final CanClearRepository<MusicListItem> mCanClearMusicListRepository;
        @NonNull
        private final MutableRepository<ShoppingListItem> mMutableShoppingListRepository;
        @NonNull
        private final CanClearRepository<ShoppingListItem> mCanClearShoppingListRepository;
        @NonNull
        private final MutableRepository<LanguagesListItem> mMutableLanguagesListRepository;
        @NonNull
        private final CanClearRepository<LanguagesListItem> mCanClearLanguagesListRepository;
        @NonNull
        private final MutableRepository<LanguagesListItemAttachedImage> mMutableLanguagesListAttachedImagesRepository;
        @NonNull
        private final AlarmsHelper mAlarmsHelper;
        @NonNull
        private final EventsHelper mEventsHelper;

        @Inject
        public Factory(
                @NonNull Application application,
                @NonNull MutableRepository<Event> mutableScheduleRepository,
                @NonNull CanClearRepository<Event> canClearScheduleRepository,
                @NonNull MutableRepository<Note> mutableNotesRepository,
                @NonNull CanClearRepository<Note> canClearNotesRepository,
                @NonNull MutableRepository<Category> mutableCategoriesRepository,
                @NonNull CanClearRepository<Category> canClearCategoriesRepository,
                @NonNull MutableRepository<UserDefinedList> mutableUserDefinedListsRepository,
                @NonNull CanClearRepository<UserDefinedList> canClearUserDefinedListsRepository,
                @NonNull MutableRepository<UserDefinedListItem> mutableUserDefinedListItemsRepository,
                @NonNull MutableRepository<MoviesListItem> mutableMoviesListRepository,
                @NonNull CanClearRepository<MoviesListItem> canClearMoviesListRepository,
                @NonNull MutableRepository<MusicListItem> mutableMusicListRepository,
                @NonNull CanClearRepository<MusicListItem> canClearMusicListRepository,
                @NonNull MutableRepository<ShoppingListItem> mutableShoppingListRepository,
                @NonNull CanClearRepository<ShoppingListItem> canClearShoppingListRepository,
                @NonNull MutableRepository<LanguagesListItem> mutableLanguagesListRepository,
                @NonNull CanClearRepository<LanguagesListItem> canClearLanguagesListRepository,
                @NonNull MutableRepository<LanguagesListItemAttachedImage> mutableLanguagesListAttachedImagesRepository,
                @NonNull AlarmsHelper alarmsHelper,
                @NonNull EventsHelper eventsHelper
        ) {
            mApplication = application;
            mMutableScheduleRepository = mutableScheduleRepository;
            mCanClearScheduleRepository = canClearScheduleRepository;
            mMutableNotesRepository = mutableNotesRepository;
            mCanClearNotesRepository = canClearNotesRepository;
            mMutableCategoriesRepository = mutableCategoriesRepository;
            mCanClearCategoriesRepository = canClearCategoriesRepository;
            mMutableUserDefinedListsRepository = mutableUserDefinedListsRepository;
            mCanClearUserDefinedListsRepository = canClearUserDefinedListsRepository;
            mMutableUserDefinedListItemsRepository = mutableUserDefinedListItemsRepository;
            mMutableMoviesListRepository = mutableMoviesListRepository;
            mCanClearMoviesListRepository = canClearMoviesListRepository;
            mMutableMusicListRepository = mutableMusicListRepository;
            mCanClearMusicListRepository = canClearMusicListRepository;
            mMutableShoppingListRepository = mutableShoppingListRepository;
            mCanClearShoppingListRepository = canClearShoppingListRepository;
            mMutableLanguagesListRepository = mutableLanguagesListRepository;
            mCanClearLanguagesListRepository = canClearLanguagesListRepository;
            mMutableLanguagesListAttachedImagesRepository = mutableLanguagesListAttachedImagesRepository;
            mAlarmsHelper = alarmsHelper;
            mEventsHelper = eventsHelper;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            ImportActivityViewModel vm = new ImportActivityViewModel(
                    mApplication,
                    mMutableScheduleRepository, mCanClearScheduleRepository,
                    mMutableNotesRepository, mCanClearNotesRepository,
                    mMutableCategoriesRepository, mCanClearCategoriesRepository,
                    mMutableUserDefinedListsRepository, mCanClearUserDefinedListsRepository,
                    mMutableUserDefinedListItemsRepository,
                    mMutableMoviesListRepository, mCanClearMoviesListRepository,
                    mMutableMusicListRepository, mCanClearMusicListRepository,
                    mMutableShoppingListRepository, mCanClearShoppingListRepository,
                    mMutableLanguagesListRepository, mCanClearLanguagesListRepository,
                    mMutableLanguagesListAttachedImagesRepository,
                    mAlarmsHelper, mEventsHelper
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
