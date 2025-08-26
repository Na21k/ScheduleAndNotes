package com.na21k.schedulenotes.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

import com.na21k.schedulenotes.BroadcastReceivers.EventNotificationAlarmReceiver;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent;
import com.na21k.schedulenotes.repositories.schedule.EventNotificationAlarmPendingIntentRepositoryImpl;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

import javax.inject.Inject;

public class AlarmsHelper {

    @NonNull
    private final Context mContext;
    @NonNull
    private final MutableRepository<Event> mScheduleRepository;

    @Inject
    public AlarmsHelper(
            @NonNull Context context,
            @NonNull MutableRepository<Event> scheduleRepository
    ) {
        mContext = context;
        mScheduleRepository = scheduleRepository;
    }

    public static void scheduleEventNotificationAlarm(int eventId,
                                                      long triggerAtMillis,
                                                      int pendingIntentRequestCode,
                                                      @NonNull Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = getEventNotificationIntent(eventId, context);
        intent.putExtra(EventNotificationAlarmReceiver
                        .EVENT_NOTIFICATION_ALARM_PENDING_INTENT_ID_INTENT_KEY,
                pendingIntentRequestCode);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                context, pendingIntentRequestCode, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, alarmIntent);
    }

    public static void cancelEventNotificationAlarmsBlocking(int eventId,
                                                             @NonNull Context context) {
        //FIXME: rewrite for instance usage with the repository injected
        EventNotificationAlarmPendingIntentRepositoryImpl pendingIntentRepository
                = new EventNotificationAlarmPendingIntentRepositoryImpl(context);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = getEventNotificationIntent(eventId, context);

        List<EventNotificationAlarmPendingIntent> pendingIntents = pendingIntentRepository
                .getByEventIdBlocking(eventId);

        for (EventNotificationAlarmPendingIntent pendingIntent : pendingIntents) {
            int pendingIntentRequestCode = pendingIntent.getId();

            PendingIntent alarmIntent = PendingIntent.getBroadcast(
                    context, pendingIntentRequestCode, intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE);

            if (alarmIntent != null) {
                alarmMgr.cancel(alarmIntent);
            }
        }
    }

    public void cancelAllEventNotificationAlarmsBlocking() {
        List<Event> events = mScheduleRepository.getAllBlocking();

        for (Event event : events) {
            cancelEventNotificationAlarmsBlocking(event.getId(), mContext);
        }
    }

    @NonNull
    private static Intent getEventNotificationIntent(int eventId, @NonNull Context context) {
        Intent intent = new Intent(context, EventNotificationAlarmReceiver.class);

        //otherwise, only extras would differ and there would be only one intent (containing the
        //last added extras) for all events.
        //NOTICE: the same intent is used for 'started' and 'starts soon' notifications but
        //there are 2 separate pending intents (this is how EventNotificationAlarmReceiver.onReceive
        //is able to tell what to say in the notification sent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            intent.setIdentifier(getEventNotificationIntentId(eventId));
        } else {
            intent.setType(getEventNotificationIntentId(eventId));
        }

        return intent;
    }

    @NonNull
    private static String getEventNotificationIntentId(int eventId) {
        return "EventNotificationAlarmReceiver.onReceive intent (eventId = " + eventId + ")";
    }
}
