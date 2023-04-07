package com.na21k.schedulenotes.helpers;

import static com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent.EventNotificationType;

import android.content.Context;

import androidx.annotation.NonNull;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventDao;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntentDao;

import java.util.Date;

public class EventsHelper {

    public static void scheduleEventNotificationsBlocking(@NonNull Event event,
                                                          @NonNull Context context) {
        EventNotificationAlarmPendingIntentDao pendingIntentDao = AppDatabase.getInstance(context)
                .eventNotificationAlarmPendingIntentDao();

        int eventId = event.getId();
        Date starts = event.getDateTimeStarts();
        Date startsSoon = DateTimeHelper.addMinutes(starts, -30);

        int startsPendingIntentRequestCode = (int) pendingIntentDao.insert(
                new EventNotificationAlarmPendingIntent(0, eventId,
                        EventNotificationAlarmPendingIntent.EventNotificationType.EventStarted));
        int startsSoonPendingIntentRequestCode = (int) pendingIntentDao.insert(
                new EventNotificationAlarmPendingIntent(0, eventId,
                        EventNotificationAlarmPendingIntent.EventNotificationType.EventStartsSoon));

        AlarmsHelper.scheduleEventNotificationAlarm(
                eventId, starts.getTime(), startsPendingIntentRequestCode, context);
        AlarmsHelper.scheduleEventNotificationAlarm(
                eventId, startsSoon.getTime(), startsSoonPendingIntentRequestCode, context);
    }

    public static void scheduleEventNotificationBlocking(
            @NonNull EventNotificationAlarmPendingIntent pendingIntent, @NonNull Context context) {
        EventDao eventDao = AppDatabase.getInstance(context).eventDao();
        Event event = eventDao.getByIdBlocking(pendingIntent.getEventId());

        Date starts = event.getDateTimeStarts();
        long triggerAtMillis;

        if (pendingIntent.getNotificationType().equals(EventNotificationType.EventStarted)) {
            triggerAtMillis = starts.getTime();
        } else {
            triggerAtMillis = DateTimeHelper.addMinutes(starts, -30).getTime();
        }

        AlarmsHelper.scheduleEventNotificationAlarm(
                event.getId(), triggerAtMillis, pendingIntent.getId(), context);
    }
}
