package com.na21k.schedulenotes.ui.schedule.eventDetails;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventDao;

import java.util.Date;
import java.util.List;

public class EventDetailsViewModel extends AndroidViewModel {

    private final EventDao mEventDao;
    private final LiveData<List<Category>> mCategories;
    private LiveData<Event> mEvent;
    private int mEventId;
    private Date mSelectedDateTimeStarts;
    private Date mSelectedDateTimeEnds;

    public EventDetailsViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mEventDao = db.eventDao();
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
        new Thread(() -> mEventDao.insert(event)).start();
    }

    public void deleteCurrentEvent() {
        new Thread(() -> mEventDao.delete(mEventId)).start();
    }

    public void updateCurrentEvent(Event event) {
        event.setId(mEventId);
        new Thread(() -> mEventDao.update(event)).start();
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
}
