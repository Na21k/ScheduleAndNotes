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

    /*@NonNull
    private String scheduleNotificationBlocking(@NonNull Event event) {
        Date starts = event.getDateTimeStarts();
        Date now = new Date();

        Data inputData = new Data.Builder()
                .putInt(EventStartsNotificationWorker.EVENT_ID_INPUT_DATA_KEY, event.getId())
                .build();

        long delayMillis = starts.getTime() - now.getTime();

        WorkRequest request = new OneTimeWorkRequest
                .Builder(EventStartsNotificationWorker.class)
                .setInputData(inputData)
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .addTag(Constants.EVENT_NOTIFICATION_WORKER_TAG)
                .build();

        String requestId = request.getId().toString();
        mNotificationDao.insert(new ScheduledNotification(0, requestId));

        WorkManager.getInstance(getApplication()).enqueue(request);

        return requestId;
    }*/

    private void scheduleNotificationsAndUpdateBlocking(@NonNull Event event) {
        WorkersHelper.scheduleEventNotificationsBlocking(event, mNotificationDao, getApplication());
        mEventDao.update(event);
    }
}
