package com.na21k.schedulenotes.repositories.schedule;

import android.content.Context;

import androidx.annotation.NonNull;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntentDao;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

import javax.inject.Inject;

public class EventNotificationAlarmPendingIntentRepositoryImpl
        extends MutableRepository<EventNotificationAlarmPendingIntent>
        implements EventNotificationAlarmPendingIntentRepository {

    private final EventNotificationAlarmPendingIntentDao mEventNotificationAlarmPendingIntentDao
            = db.eventNotificationAlarmPendingIntentDao();

    @Inject
    public EventNotificationAlarmPendingIntentRepositoryImpl(@NonNull Context context) {
        super(context);
    }

    @Override
    public List<EventNotificationAlarmPendingIntent> getByEventIdBlocking(int eventId) {
        return mEventNotificationAlarmPendingIntentDao.getByEventIdBlocking(eventId);
    }

    @Override
    protected BaseDao<EventNotificationAlarmPendingIntent> getDao() {
        return mEventNotificationAlarmPendingIntentDao;
    }
}
