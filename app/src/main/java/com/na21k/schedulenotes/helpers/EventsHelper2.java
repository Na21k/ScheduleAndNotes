package com.na21k.schedulenotes.helpers;

import android.content.Context;

import androidx.annotation.NonNull;

import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.schedule.EventNotificationAlarmPendingIntentRepository;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class EventsHelper2 {

    private static final int EVENT_STARTS_SOON_TIME_OFFSET_MINS = -30;
    @NonNull
    private final Context mContext;
    @NonNull
    private final MutableRepository<Event> mMutableScheduleRepository;
    @NonNull
    private final MutableRepository<EventNotificationAlarmPendingIntent> mMutablePendingIntentRepository;
    @NonNull
    private final EventNotificationAlarmPendingIntentRepository mPendingIntentRepository;

    @Inject
    public EventsHelper2(
            @NonNull Context context,
            @NonNull MutableRepository<Event> mutableScheduleRepository,
            @NonNull MutableRepository<EventNotificationAlarmPendingIntent> mutablePendingIntentRepository,
            @NonNull EventNotificationAlarmPendingIntentRepository pendingIntentRepository
    ) {
        mContext = context;
        mMutableScheduleRepository = mutableScheduleRepository;
        mMutablePendingIntentRepository = mutablePendingIntentRepository;
        mPendingIntentRepository = pendingIntentRepository;
    }

    /**
     * Updates the Event and reschedules the notifications. The longevity of the Event is preserved.
     */
    public void postponeToAsync(@NonNull Event event, @NonNull Date dateOnly) {
        new Thread(() -> postponeToBlocking(event, dateOnly)).start();
    }

    /**
     * Updates the Event and reschedules the notifications. The longevity of the Event is preserved.
     */
    public void postponeToBlocking(@NonNull Event event, @NonNull Date dateOnly) {
        Date newStartsDateOnly = DateTimeHelper.truncateToDateOnly(dateOnly);
        Date oldStartsDateOnly = DateTimeHelper.truncateToDateOnly(event.getDateTimeStarts());
        Date oldEndsDateOnly = DateTimeHelper.truncateToDateOnly(event.getDateTimeEnds());

        Date postponeDaysDiff = DateTimeHelper.getDifference(oldStartsDateOnly, newStartsDateOnly);
        Date newEndsDateOnly = DateTimeHelper.addDates(oldEndsDateOnly, postponeDaysDiff);

        Date startsTimeOnly = DateTimeHelper.getTimeOnly(event.getDateTimeStarts());
        Date endsTimeOnly = DateTimeHelper.getTimeOnly(event.getDateTimeEnds());

        Date newDateTimeStarts = DateTimeHelper.addDates(newStartsDateOnly, startsTimeOnly);
        Date newDateTimeEnds = DateTimeHelper.addDates(newEndsDateOnly, endsTimeOnly);

        postponeToBlocking(event, newDateTimeStarts, newDateTimeEnds);
    }

    /**
     * Updates the Event and reschedules the notifications
     */
    private void postponeToBlocking(
            @NonNull Event event,
            @NonNull Date newDateTimeStarts,
            @NonNull Date newDateTimeEnds
    ) {
        cancelEventNotificationsBlocking(event);

        event.setDateTimeStarts(newDateTimeStarts);
        event.setDateTimeEnds(newDateTimeEnds);
        mMutableScheduleRepository.update(event);

        scheduleEventNotificationsBlocking(event);
    }

    public void scheduleEventNotificationsBlocking(@NonNull Event event) {
        int eventId = event.getId();
        Date starts = event.getDateTimeStarts();
        Date startsSoon = getEventStartsSoonDateTime(starts);

        int startsSoonPendingIntentRequestCode = (int) mMutablePendingIntentRepository.addBlocking(
                new EventNotificationAlarmPendingIntent(0, eventId,
                        EventNotificationAlarmPendingIntent.EventNotificationType.EventStartsSoon));
        int startsPendingIntentRequestCode = (int) mMutablePendingIntentRepository.addBlocking(
                new EventNotificationAlarmPendingIntent(0, eventId,
                        EventNotificationAlarmPendingIntent.EventNotificationType.EventStarted));

        AlarmsHelper.scheduleEventNotificationAlarm(
                eventId, startsSoon.getTime(), startsSoonPendingIntentRequestCode, mContext);
        AlarmsHelper.scheduleEventNotificationAlarm(
                eventId, starts.getTime(), startsPendingIntentRequestCode, mContext);
    }

    private void cancelEventNotificationsBlocking(@NonNull Event event) {
        AlarmsHelper.cancelEventNotificationAlarmsBlocking(event.getId(), mContext);

        List<EventNotificationAlarmPendingIntent> pendingIntents = mPendingIntentRepository
                .getByEventIdBlocking(event.getId());

        for (EventNotificationAlarmPendingIntent pendingIntent : pendingIntents) {
            mMutablePendingIntentRepository.deleteBlocking(pendingIntent);
        }
    }

    public void ensureEventNotificationsScheduledBlocking() {
        //only pending intents for notifications that haven't been sent yet are stored
        List<EventNotificationAlarmPendingIntent> pendingIntents = mMutablePendingIntentRepository
                .getAllBlocking();

        for (EventNotificationAlarmPendingIntent pendingIntent : pendingIntents) {
            scheduleEventNotificationBlocking(pendingIntent);
        }
    }

    private void scheduleEventNotificationBlocking(
            @NonNull EventNotificationAlarmPendingIntent pendingIntent
    ) {
        EventNotificationAlarmPendingIntent.EventNotificationType notificationType = pendingIntent
                .getNotificationType();
        Event event = mMutableScheduleRepository.getByIdBlocking(pendingIntent.getEventId());

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
                event.getId(), triggerAtMillis, pendingIntent.getId(), mContext);
    }

    @NonNull
    private static Date getEventStartsSoonDateTime(@NonNull Date eventsStarts) {
        return DateTimeHelper.addMinutes(eventsStarts, EVENT_STARTS_SOON_TIME_OFFSET_MINS);
    }
}
