package com.na21k.schedulenotes.repositories;

import android.content.Context;

import androidx.annotation.NonNull;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntentDao;

import java.util.List;

public class EventNotificationAlarmPendingIntentRepository
        extends MutableRepository<EventNotificationAlarmPendingIntent> {

    private final EventNotificationAlarmPendingIntentDao mEventNotificationAlarmPendingIntentDao
            = db.eventNotificationAlarmPendingIntentDao();

    public EventNotificationAlarmPendingIntentRepository(@NonNull Context context) {
        super(context);
    }

    public List<EventNotificationAlarmPendingIntent> getByEventIdBlocking(int eventId) {
        return mEventNotificationAlarmPendingIntentDao.getByEventIdBlocking(eventId);
    }

    @Override
    protected BaseDao<EventNotificationAlarmPendingIntent> getDao() {
        return mEventNotificationAlarmPendingIntentDao;
    }
}
