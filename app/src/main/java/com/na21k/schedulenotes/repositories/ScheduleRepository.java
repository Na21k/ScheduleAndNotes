package com.na21k.schedulenotes.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventDao;

import java.util.Date;
import java.util.List;

public class ScheduleRepository extends MutableRepository<Event>
        implements CanSearchRepository<Event>, CanClearRepository {

    private final EventDao mEventDao = db.eventDao();

    public ScheduleRepository(@NonNull Context context) {
        super(context);
    }

    public LiveData<List<Event>> getByDate(Date hasStartedBefore, Date hasNotEndedBy) {
        return mEventDao.getByDate(hasStartedBefore, hasNotEndedBy);
    }

    @Override
    public LiveData<List<Event>> getSearch(String query) {
        return mEventDao.search(query);
    }

    public Task<Void> clearOlderThan(Date date) {
        return runSimpleAsync(() -> mEventDao.deleteOlderThan(date));
    }

    @Override
    public void clearBlocking() {
        mEventDao.deleteAll();
    }

    @Override
    protected BaseDao<Event> getDao() {
        return mEventDao;
    }
}
