package com.na21k.schedulenotes.ui.schedule;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventDao;
import com.na21k.schedulenotes.helpers.WorkersHelper;

import java.util.Date;
import java.util.List;

public class ScheduleViewModel extends AndroidViewModel {

    private final EventDao mEventDao;
    private final LiveData<List<Category>> mAllCategories;
    private List<Event> mEventsCache = null;
    private List<Category> mCategoriesCache = null;
    private Date mSelectedDate = null;

    public ScheduleViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mEventDao = db.eventDao();
        mAllCategories = db.categoryDao().getAll();
    }

    public LiveData<List<Event>> getByDate(Date hasStartedBefore, Date hasNotEndedBy) {
        return mEventDao.getByDate(hasStartedBefore, hasNotEndedBy);
    }

    public LiveData<List<Event>> getEventsSearch(String searchQuery) {
        return mEventDao.search(searchQuery);
    }

    public LiveData<List<Category>> getAllCategories() {
        return mAllCategories;
    }

    public void createEvent(Event event) {
        new Thread(() -> mEventDao.insert(event)).start();
    }

    public void updateEvent(Event event) {
        new Thread(() -> mEventDao.update(event)).start();
    }

    public void deleteEvent(Event event) {
        WorkersHelper.cancelRequest(event.getLastStartsNotificationRequestId(),
                getApplication());
        WorkersHelper.cancelRequest(event.getLastStartsSoonNotificationRequestId(),
                getApplication());

        new Thread(() -> mEventDao.delete(event)).start();
    }

    public List<Event> getEventsCache() {
        return mEventsCache;
    }

    public void setEventsCache(List<Event> eventsCache) {
        mEventsCache = eventsCache;
    }

    public List<Category> getCategoriesCache() {
        return mCategoriesCache;
    }

    public void setCategoriesCache(List<Category> categoriesCache) {
        mCategoriesCache = categoriesCache;
    }

    public Date getSelectedDate() {
        return mSelectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        mSelectedDate = selectedDate;
    }
}
