package com.na21k.schedulenotes.ui.schedule.eventDetails;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.helpers.AlarmsHelper;
import com.na21k.schedulenotes.helpers.EventsHelper;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.Repository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

public class EventDetailsViewModel extends ViewModel {

    @NonNull
    private final MutableRepository<Event> mMutableScheduleRepository;
    private final int mEventId;
    @NonNull
    private final LiveData<Event> mEvent;
    @NonNull
    private final LiveData<List<Category>> mCategories;
    @NonNull
    private final AlarmsHelper mAlarmsHelper;
    @NonNull
    private final EventsHelper mEventsHelper;

    private EventDetailsViewModel(
            @NonNull MutableRepository<Event> mutableScheduleRepository,
            int eventId,
            @NonNull Repository<Category> categoriesRepository,
            @NonNull AlarmsHelper alarmsHelper,
            @NonNull EventsHelper eventsHelper
    ) {
        super();

        mMutableScheduleRepository = mutableScheduleRepository;
        mEventId = eventId;

        mEvent = mMutableScheduleRepository.getById(eventId);
        mCategories = categoriesRepository.getAll();

        mAlarmsHelper = alarmsHelper;
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
                mAlarmsHelper.cancelEventNotificationAlarmsBlocking(event.getId());
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
            mAlarmsHelper.cancelEventNotificationAlarmsBlocking(mEventId);
            mMutableScheduleRepository.delete(mEventId);
        }).start();
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final MutableRepository<Event> mMutableScheduleRepository;
        @NonNull
        private final Repository<Category> mCategoriesRepository;
        private final int mEventId;
        @NonNull
        private final AlarmsHelper mAlarmsHelper;
        @NonNull
        private final EventsHelper mEventsHelper;

        @AssistedInject
        public Factory(
                @NonNull MutableRepository<Event> mutableScheduleRepository,
                @Assisted int eventId,
                @NonNull Repository<Category> categoriesRepository,
                @NonNull AlarmsHelper alarmsHelper,
                @NonNull EventsHelper eventsHelper
        ) {
            mMutableScheduleRepository = mutableScheduleRepository;
            mCategoriesRepository = categoriesRepository;
            mEventId = eventId;
            mAlarmsHelper = alarmsHelper;
            mEventsHelper = eventsHelper;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            EventDetailsViewModel vm = new EventDetailsViewModel(
                    mMutableScheduleRepository, mEventId, mCategoriesRepository,
                    mAlarmsHelper, mEventsHelper
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
