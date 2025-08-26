package com.na21k.schedulenotes.repositories.schedule;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventDao;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class ScheduleRepositoryImpl extends MutableRepository<Event>
        implements ScheduleRepository, CanSearchRepository<Event>, CanClearRepository<Event> {

    private final EventDao mEventDao = db.eventDao();

    @Inject
    public ScheduleRepositoryImpl(@NonNull Context context) {
        super(context);
    }

    @Override
    protected BaseDao<Event> getDao() {
        return mEventDao;
    }

    @Override
    public LiveData<List<Event>> getByDate(Date hasStartedBefore, Date hasNotEndedBy) {
        return mEventDao.getByDate(hasStartedBefore, hasNotEndedBy);
    }

    @Override
    public Task<Void> clearOlderThan(Date date) {
        return runSimpleAsync(() -> mEventDao.deleteOlderThan(date));
    }

    @Override
    public LiveData<List<Event>> getSearch(String query) {
        return mEventDao.search(query);
    }

    @Override
    public void clearBlocking() {
        mEventDao.deleteAll();
    }
}
