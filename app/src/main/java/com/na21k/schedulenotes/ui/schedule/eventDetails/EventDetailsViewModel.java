package com.na21k.schedulenotes.ui.schedule.eventDetails;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.helpers.AlarmsHelper;
import com.na21k.schedulenotes.helpers.EventsHelper;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.repositories.ScheduleRepository;

import java.util.Date;
import java.util.List;

public class EventDetailsViewModel extends AndroidViewModel {

    private final ScheduleRepository mScheduleRepository;
    private final LiveData<List<Category>> mCategories;
    private List<Category> mCategoriesCache = null;
    private LiveData<Event> mEvent;
    private int mEventId;
    private Date mSelectedDateTimeStarts;
    private Date mSelectedDateTimeEnds;

    public EventDetailsViewModel(@NonNull Application application) {
        super(application);

        mScheduleRepository = new ScheduleRepository(application);
        CategoriesRepository categoriesRepository = new CategoriesRepository(application);

        mCategories = categoriesRepository.getAll();
    }

    public LiveData<Event> getEvent(int id) {
        if (mEventId != id) {
            mEvent = mScheduleRepository.getById(id);
            mEventId = id;
        }

        return mEvent;
    }

    public LiveData<List<Category>> getAllCategories() {
        return mCategories;
    }

    public void createEvent(Event event) {
        mScheduleRepository.add(event).addOnSuccessListener(id -> {
            event.setId(Math.toIntExact(id));
            new Thread(() -> scheduleNotificationsAndUpdateBlocking(event)).start();
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
        new Thread(() -> {
            AlarmsHelper.cancelEventNotificationAlarmsBlocking(event.getId(), getApplication());
            scheduleNotificationsAndUpdateBlocking(event);
        }).start();
    }

    @Nullable
    public LiveData<Event> getCurrentEvent() {
        return mEvent;
    }

    @Nullable
    public Date getSelectedDateTimeStarts() {
        return mSelectedDateTimeStarts;
    }

    public void setSelectedDateTimeStarts(@NonNull Date selectedDateTimeStarts) {
        this.mSelectedDateTimeStarts = selectedDateTimeStarts;
    }

    @Nullable
    public Date getSelectedDateTimeEnds() {
        return mSelectedDateTimeEnds;
    }

    public void setSelectedDateTimeEnds(@NonNull Date selectedDateTimeEnds) {
        mSelectedDateTimeEnds = selectedDateTimeEnds;
    }

    public List<Category> getCategoriesCache() {
        return mCategoriesCache;
    }

    public void setCategoriesCache(List<Category> categoriesCache) {
        mCategoriesCache = categoriesCache;
    }

    private void scheduleNotificationsAndUpdateBlocking(@NonNull Event event) {
        EventsHelper.scheduleEventNotificationsBlocking(event, getApplication());
        mScheduleRepository.update(event);
    }
}
