package com.na21k.schedulenotes.ui.schedule.eventDetails;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.helpers.AlarmsHelper;
import com.na21k.schedulenotes.helpers.EventsHelper2;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

public class EventDetailsViewModel extends AndroidViewModel {

    @NonNull
    private final MutableRepository<Event> mMutableScheduleRepository;
    private final int mEventId;
    @NonNull
    private final LiveData<Event> mEvent;
    @NonNull
    private final LiveData<List<Category>> mCategories;
    @NonNull
    private final EventsHelper2 mEventsHelper;

    private EventDetailsViewModel(
            @NonNull Application application,
            @NonNull MutableRepository<Event> mutableScheduleRepository,
            int eventId,
            @NonNull CategoriesRepository categoriesRepository,
            @NonNull EventsHelper2 eventsHelper
    ) {
        super(application);

        mMutableScheduleRepository = mutableScheduleRepository;
        mEventId = eventId;

        mEvent = mMutableScheduleRepository.getById(eventId);
        mCategories = categoriesRepository.getAll();

        mEventsHelper = eventsHelper;
    }

    public boolean isEditing() {
        return mEventId != 0;
    }

    @NonNull
    public LiveData<Event> getEvent() {
        return mEvent;
    }

    @NonNull
    public LiveData<List<Category>> getCategories() {
        return mCategories;
    }

    public void saveEvent(@NonNull Event event) {
        event.setId(mEventId);

        if (isEditing()) {
            mMutableScheduleRepository.update(event);
            new Thread(() -> {
                AlarmsHelper.cancelEventNotificationAlarmsBlocking(event.getId(), getApplication());
                mEventsHelper.scheduleEventNotificationsBlocking(event);
            }).start();
        } else {
            mMutableScheduleRepository.add(event).addOnSuccessListener(id -> {
                event.setId(Math.toIntExact(id));
                new Thread(() ->
                        mEventsHelper.scheduleEventNotificationsBlocking(event)
                ).start();
            });
        }
    }

    public void deleteEvent() {
        new Thread(() -> {
            AlarmsHelper.cancelEventNotificationAlarmsBlocking(mEventId, getApplication());
            mMutableScheduleRepository.delete(mEventId);
        }).start();
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final Application mApplication;
        @NonNull
        private final MutableRepository<Event> mMutableScheduleRepository;
        @NonNull
        private final CategoriesRepository mCategoriesRepository;
        private final int mEventId;
        @NonNull
        private final EventsHelper2 mEventsHelper;

        @AssistedInject
        public Factory(
                @NonNull Application application,
                @NonNull MutableRepository<Event> mutableScheduleRepository,
                @Assisted int eventId,
                @NonNull CategoriesRepository categoriesRepository,
                @NonNull EventsHelper2 eventsHelper
        ) {
            mApplication = application;
            mMutableScheduleRepository = mutableScheduleRepository;
            mCategoriesRepository = categoriesRepository;
            mEventId = eventId;
            mEventsHelper = eventsHelper;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            EventDetailsViewModel vm = new EventDetailsViewModel(
                    mApplication,
                    mMutableScheduleRepository, mEventId, mCategoriesRepository,
                    mEventsHelper
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }

        @dagger.assisted.AssistedFactory
        public interface AssistedFactory {

            Factory create(int eventId);
        }
    }
}
