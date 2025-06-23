package com.na21k.schedulenotes.ui.schedule.eventDetails;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.helpers.AlarmsHelper;
import com.na21k.schedulenotes.helpers.EventsHelper;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.repositories.ScheduleRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class EventDetailsViewModel extends AndroidViewModel {

    @NonNull
    private final ScheduleRepository mScheduleRepository;
    @NonNull
    private final LiveData<List<Category>> mCategories;
    private List<Category> mCategoriesCache = null;
    @Nullable
    private LiveData<Event> mEvent;
    private int mEventId;

    private EventDetailsViewModel(
            @NonNull Application application,
            @NonNull ScheduleRepository scheduleRepository,
            @NonNull CategoriesRepository categoriesRepository
    ) {
        super(application);

        mScheduleRepository = scheduleRepository;

        mCategories = categoriesRepository.getAll();
    }

    public LiveData<Event> getEvent(int id) {
        if (mEventId != id) {
            mEvent = mScheduleRepository.getById(id);
            mEventId = id;
        }

        return mEvent;
    }

    @NonNull
    public LiveData<List<Category>> getAllCategories() {
        return mCategories;
    }

    public void createEvent(Event event) {
        mScheduleRepository.add(event).addOnSuccessListener(id -> {
            event.setId(Math.toIntExact(id));
            new Thread(() ->
                    EventsHelper.scheduleEventNotificationsBlocking(event, getApplication())
            ).start();
        });
    }

    public void deleteCurrentEvent() {
        new Thread(() -> {
            AlarmsHelper.cancelEventNotificationAlarmsBlocking(mEventId, getApplication());
            mScheduleRepository.delete(mEventId);
        }).start();
    }

    public void updateCurrentEvent(Event event) {
        event.setId(mEventId);
        mScheduleRepository.update(event);
        new Thread(() -> {
            AlarmsHelper.cancelEventNotificationAlarmsBlocking(event.getId(), getApplication());
            EventsHelper.scheduleEventNotificationsBlocking(event, getApplication());
        }).start();
    }

    @Nullable
    public LiveData<Event> getCurrentEvent() {
        return mEvent;
    }

    public List<Category> getCategoriesCache() {
        return mCategoriesCache;
    }

    public void setCategoriesCache(List<Category> categoriesCache) {
        mCategoriesCache = categoriesCache;
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final Application mApplication;
        @NonNull
        private final ScheduleRepository mScheduleRepository;
        @NonNull
        private final CategoriesRepository mCategoriesRepository;

        @Inject
        public Factory(
                @NonNull Application application,
                @NonNull ScheduleRepository scheduleRepository,
                @NonNull CategoriesRepository categoriesRepository
        ) {
            mApplication = application;
            mScheduleRepository = scheduleRepository;
            mCategoriesRepository = categoriesRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            EventDetailsViewModel vm = new EventDetailsViewModel(
                    mApplication, mScheduleRepository, mCategoriesRepository
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
