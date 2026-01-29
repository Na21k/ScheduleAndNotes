package com.na21k.schedulenotes.repositories.schedule;

import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.na21k.schedulenotes.data.database.Schedule.Event;

import java.util.Date;
import java.util.List;

public interface ScheduleRepository {

    LiveData<List<Event>> getByDate(Date hasStartedBefore, Date hasNotEndedBy);

    Task<Void> clearOlderThan(Date date);
}
