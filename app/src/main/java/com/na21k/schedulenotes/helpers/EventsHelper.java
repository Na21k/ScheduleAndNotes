package com.na21k.schedulenotes.helpers;

import static com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent.EventNotificationType;

import android.content.Context;

import androidx.annotation.NonNull;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventDao;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntentDao;

import java.util.Date;
import java.util.List;

public class EventsHelper {

    public static void postponeToAsync(@NonNull Event event, @NonNull Date dateOnly,
                                       @NonNull Context context) {
        new Thread(() -> postponeToBlocking(event, dateOnly, context)).start();
    }

    public static void postponeToBlocking(@NonNull Event event, @NonNull Date dateOnly,
                                          @NonNull Context context) {
        Date newStartsDateOnly = DateTimeHelper.truncateToDateOnly(dateOnly);
        Date oldStartsDateOnly = DateTimeHelper.truncateToDateOnly(event.getDateTimeStarts());
        Date oldEndsDateOnly = DateTimeHelper.truncateToDateOnly(event.getDateTimeEnds());

        Date postponeDaysDiff = DateTimeHelper.getDifference(oldStartsDateOnly, newStartsDateOnly);
        Date newEndsDateOnly = DateTimeHelper.addDates(oldEndsDateOnly, postponeDaysDiff);

        Date startsTimeOnly = DateTimeHelper.getTimeOnly(event.getDateTimeStarts());
        Date endsTimeOnly = DateTimeHelper.getTimeOnly(event.getDateTimeEnds());

        Date newDateTimeStarts = DateTimeHelper.addDates(newStartsDateOnly, startsTimeOnly);
        Date newDateTimeEnds = DateTimeHelper.addDates(newEndsDateOnly, endsTimeOnly);

        postponeToBlocking(event, newDateTimeStarts, newDateTimeEnds, context);
    }

    private static void postponeToBlocking(@NonNull Event event, @NonNull Date newDateTimeStarts,
                                           @NonNull Date newDateTimeEnds, @NonNull Context context) {
        cancelEventNotificationsBlocking(event, context);

        event.setDateTimeStarts(newDateTimeStarts);
        event.setDateTimeEnds(newDateTimeEnds);
        AppDatabase.getInstance(context).eventDao().update(event);

        scheduleEventNotificationsBlocking(event, context);
    }

    public static void scheduleEventNotificationsBlocking(@NonNull Event event,
                                                          @NonNull Context context) {
        EventNotificationAlarmPendingIntentDao pendingIntentDao = AppDatabase.getInstance(context)
                .eventNotificationAlarmPendingIntentDao();

        int eventId = event.getId();
        Date starts = event.getDateTimeStarts();
        Date startsSoon = Constants.getEventStartsSoonNotificationTime(starts);

        int startsSoonPendingIntentRequestCode = (int) pendingIntentDao.insert(
                new EventNotificationAlarmPendingIntent(0, eventId,
                        EventNotificationAlarmPendingIntent.EventNotificationType.EventStartsSoon));
        int startsPendingIntentRequestCode = (int) pendingIntentDao.insert(
                new EventNotificationAlarmPendingIntent(0, eventId,
                        EventNotificationAlarmPendingIntent.EventNotificationType.EventStarted));

        AlarmsHelper.scheduleEventNotificationAlarm(
                eventId, startsSoon.getTime(), startsSoonPendingIntentRequestCode, context);
        AlarmsHelper.scheduleEventNotificationAlarm(
                eventId, starts.getTime(), startsPendingIntentRequestCode, context);
    }

    public static void scheduleEventNotificationBlocking(
            @NonNull EventNotificationAlarmPendingIntent pendingIntent, @NonNull Context context) {
        EventNotificationType notificationType = pendingIntent.getNotificationType();
        EventDao eventDao = AppDatabase.getInstance(context).eventDao();
        Event event = eventDao.getByIdBlocking(pendingIntent.getEventId());

        Date starts = event.getDateTimeStarts();
        long triggerAtMillis;

        switch (notificationType) {
            case EventStarted:
                triggerAtMillis = starts.getTime();
                break;
            case EventStartsSoon:
                triggerAtMillis = Constants
                        .getEventStartsSoonNotificationTime(starts).getTime();
                break;
            default:
                throw new IllegalArgumentException(
                        "Unexpected notification type: " + notificationType.name());
        }

        AlarmsHelper.scheduleEventNotificationAlarm(
                event.getId(), triggerAtMillis, pendingIntent.getId(), context);
    }

    public static void cancelEventNotificationsBlocking(@NonNull Event event,
                                                        @NonNull Context context) {
        EventNotificationAlarmPendingIntentDao pendingIntentDao = AppDatabase.getInstance(context)
                .eventNotificationAlarmPendingIntentDao();

        List<EventNotificationAlarmPendingIntent> pendingIntents = pendingIntentDao
                .getByEventIdBlocking(event.getId());

        for (EventNotificationAlarmPendingIntent pendingIntent : pendingIntents) {
            scheduleEventNotificationBlocking(pendingIntent, context);
        }
    }

    public static void ensureEventNotificationsScheduledBlocking(@NonNull Context context) {
        EventNotificationAlarmPendingIntentDao pendingIntentDao = AppDatabase.getInstance(context)
                .eventNotificationAlarmPendingIntentDao();
        //only pending intents for notifications that haven't been sent yet are stored
        List<EventNotificationAlarmPendingIntent> pendingIntents = pendingIntentDao
                .getAllBlocking();

        for (EventNotificationAlarmPendingIntent pendingIntent : pendingIntents) {
            scheduleEventNotificationBlocking(pendingIntent, context);
        }
    }
}
