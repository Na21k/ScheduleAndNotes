package com.na21k.schedulenotes.ui.schedule.eventDetails;

import android.app.Application;

import androidx.annotation.NonNull;
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

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

public class EventDetailsViewModel extends AndroidViewModel {

    @NonNull
    private final ScheduleRepository mScheduleRepository;
    private final int mEventId;
    @NonNull
    private final LiveData<Event> mEvent;
    @NonNull
    private final LiveData<List<Category>> mCategories;

    private EventDetailsViewModel(
            @NonNull Application application,
            @NonNull ScheduleRepository scheduleRepository,
            int eventId,
            @NonNull CategoriesRepository categoriesRepository
    ) {
        super(application);

        mScheduleRepository = scheduleRepository;
        mEventId = eventId;

        mEvent = scheduleRepository.getById(eventId);
        mCategories = categoriesRepository.getAll();
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
        event.setId(isEditing() ? mEventId : 0);

        if (isEditing()) {
            mScheduleRepository.update(event);
            new Thread(() -> {
                AlarmsHelper.cancelEventNotificationAlarmsBlocking(event.getId(), getApplication());
                EventsHelper.scheduleEventNotificationsBlocking(event, getApplication());
            }).start();
        } else {
            mScheduleRepository.add(event).addOnSuccessListener(id -> {
                event.setId(Math.toIntExact(id));
                new Thread(() ->
                        EventsHelper.scheduleEventNotificationsBlocking(event, getApplication())
                ).start();
            });
        }
    }

    public void deleteEvent() {
        new Thread(() -> {
            AlarmsHelper.cancelEventNotificationAlarmsBlocking(mEventId, getApplication());
            mScheduleRepository.delete(mEventId);
        }).start();
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final Application mApplication;
        @NonNull
        private final ScheduleRepository mScheduleRepository;
        @NonNull
        private final CategoriesRepository mCategoriesRepository;
        private final int mEventId;

        @AssistedInject
        public Factory(
                @NonNull Application application,
                @NonNull ScheduleRepository scheduleRepository,
                @Assisted int eventId,
                @NonNull CategoriesRepository categoriesRepository
        ) {
            mApplication = application;
            mScheduleRepository = scheduleRepository;
            mCategoriesRepository = categoriesRepository;
            mEventId = eventId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            EventDetailsViewModel vm = new EventDetailsViewModel(
                    mApplication, mScheduleRepository, mEventId, mCategoriesRepository
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
