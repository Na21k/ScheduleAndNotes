package com.na21k.schedulenotes.ui.schedule.eventDetails;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notifications.ScheduledNotificationDao;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventDao;
import com.na21k.schedulenotes.helpers.WorkersHelper;

import java.util.Date;
import java.util.List;

public class EventDetailsViewModel extends AndroidViewModel {

    private final EventDao mEventDao;
    private final ScheduledNotificationDao mNotificationDao;
    private final LiveData<List<Category>> mCategories;
    private List<Category> mCategoriesCache = null;
    private LiveData<Event> mEvent;
    private int mEventId;
    private Date mSelectedDateTimeStarts;
    private Date mSelectedDateTimeEnds;

    public EventDetailsViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mEventDao = db.eventDao();
        mNotificationDao = db.scheduledNotificationDao();
        mCategories = db.categoryDao().getAll();
    }

    public LiveData<Event> getEvent(int id) {
        if (mEventId != id) {
            mEvent = mEventDao.getById(id);
            mEventId = id;
        }

        return mEvent;
    }

    public LiveData<List<Category>> getAllCategories() {
        return mCategories;
    }

    public void createEvent(Event event) {
        new Thread(() -> {
            long id = mEventDao.insert(event);
            event.setId((int) id);
            scheduleNotificationsAndUpdateBlocking(event);
        }).start();
    }

    public void deleteCurrentEvent() {
        new Thread(() -> mEventDao.delete(mEventId)).start();
    }

    public void updateCurrentEvent(Event event) {
        event.setId(mEventId);
        new Thread(() -> {
            WorkersHelper.cancelRequest(event.getLastStartsNotificationRequestId(),
                    getApplication());
            WorkersHelper.cancelRequest(event.getLastStartsSoonNotificationRequestId(),
                    getApplication());
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
        WorkersHelper.scheduleEventNotificationsBlocking(event, mNotificationDao, getApplication());
        mEventDao.update(event);
    }
}
