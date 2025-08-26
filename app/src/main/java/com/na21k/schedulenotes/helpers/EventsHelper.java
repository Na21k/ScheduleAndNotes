package com.na21k.schedulenotes.helpers;

import static com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent.EventNotificationType;

import android.content.Context;

import androidx.annotation.NonNull;

import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent;
import com.na21k.schedulenotes.repositories.schedule.EventNotificationAlarmPendingIntentRepositoryImpl;
import com.na21k.schedulenotes.repositories.schedule.ScheduleRepositoryImpl;

import java.util.Date;
import java.util.List;

//switch to EventsHelper2
@Deprecated(forRemoval = true)
public class EventsHelper {

    public static final int EVENT_STARTS_SOON_TIME_OFFSET_MINS = -30;

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
        new ScheduleRepositoryImpl(context).update(event);

        scheduleEventNotificationsBlocking(event, context);
    }

    public static void scheduleEventNotificationsBlocking(@NonNull Event event,
                                                          @NonNull Context context) {
        EventNotificationAlarmPendingIntentRepositoryImpl pendingIntentRepository
                = new EventNotificationAlarmPendingIntentRepositoryImpl(context);

        int eventId = event.getId();
        Date starts = event.getDateTimeStarts();
        Date startsSoon = getEventStartsSoonDateTime(starts);

        int startsSoonPendingIntentRequestCode = (int) pendingIntentRepository.addBlocking(
                new EventNotificationAlarmPendingIntent(0, eventId,
                        EventNotificationAlarmPendingIntent.EventNotificationType.EventStartsSoon));
        int startsPendingIntentRequestCode = (int) pendingIntentRepository.addBlocking(
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
        ScheduleRepositoryImpl scheduleRepository = new ScheduleRepositoryImpl(context);
        Event event = scheduleRepository.getByIdBlocking(pendingIntent.getEventId());

        Date starts = event.getDateTimeStarts();
        long triggerAtMillis;

        switch (notificationType) {
            case EventStarted:
                triggerAtMillis = starts.getTime();
                break;
            case EventStartsSoon:
                triggerAtMillis = getEventStartsSoonDateTime(starts).getTime();
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
        AlarmsHelper.cancelEventNotificationAlarmsBlocking(event.getId(), context);

        EventNotificationAlarmPendingIntentRepositoryImpl pendingIntentRepository
                = new EventNotificationAlarmPendingIntentRepositoryImpl(context);
        List<EventNotificationAlarmPendingIntent> pendingIntents = pendingIntentRepository
                .getByEventIdBlocking(event.getId());

        for (EventNotificationAlarmPendingIntent pendingIntent : pendingIntents) {
            pendingIntentRepository.deleteBlocking(pendingIntent);
        }
    }

    public static void ensureEventNotificationsScheduledBlocking(@NonNull Context context) {
        EventNotificationAlarmPendingIntentRepositoryImpl pendingIntentRepository
                = new EventNotificationAlarmPendingIntentRepositoryImpl(context);
        //only pending intents for notifications that haven't been sent yet are stored
        List<EventNotificationAlarmPendingIntent> pendingIntents = pendingIntentRepository
                .getAllBlocking();

        for (EventNotificationAlarmPendingIntent pendingIntent : pendingIntents) {
            scheduleEventNotificationBlocking(pendingIntent, context);
        }
    }

    @NonNull
    public static Date getEventStartsSoonDateTime(Date eventsStarts) {
        return DateTimeHelper.addMinutes(eventsStarts, EVENT_STARTS_SOON_TIME_OFFSET_MINS);
    }
}
