package com.na21k.schedulenotes.repositories;

import android.content.Context;

import androidx.annotation.NonNull;

import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntentDao;

import java.util.List;

public class EventNotificationAlarmPendingIntentRepository extends Repository {

    private final EventNotificationAlarmPendingIntentDao mEventNotificationAlarmPendingIntentDao
            = db.eventNotificationAlarmPendingIntentDao();

    public EventNotificationAlarmPendingIntentRepository(@NonNull Context context) {
        super(context);
    }

    public EventNotificationAlarmPendingIntent getByIdBlocking(int id) {
        return mEventNotificationAlarmPendingIntentDao.getByIdBlocking(id);
    }

    public List<EventNotificationAlarmPendingIntent> getByEventIdBlocking(int eventId) {
        return mEventNotificationAlarmPendingIntentDao.getByEventIdBlocking(eventId);
    }

    public List<EventNotificationAlarmPendingIntent> getAllBlocking() {
        return mEventNotificationAlarmPendingIntentDao.getAllBlocking();
    }

    public long addBlocking(EventNotificationAlarmPendingIntent item) {
        return mEventNotificationAlarmPendingIntentDao.insert(item);
    }

    public void deleteBlocking(EventNotificationAlarmPendingIntent item) {
        mEventNotificationAlarmPendingIntentDao.delete(item);
    }
}
