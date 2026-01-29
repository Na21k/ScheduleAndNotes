package com.na21k.schedulenotes.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.na21k.schedulenotes.BroadcastReceivers.EventNotificationAlarmReceiver;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.schedule.eventNotificationAlarmPendingIntents.EventNotificationAlarmPendingIntentRepository;

import java.util.List;

import javax.inject.Inject;

public class AlarmsHelper {

    @NonNull
    private final Context mContext;
    @NonNull
    private final AlarmManager mAlarmMgr;
    @NonNull
    private final MutableRepository<Event> mScheduleRepository;
    @NonNull
    private final EventNotificationAlarmPendingIntentRepository mPendingIntentRepository;

    @Inject
    public AlarmsHelper(
            @NonNull Context context,
            @NonNull MutableRepository<Event> scheduleRepository,
            @NonNull EventNotificationAlarmPendingIntentRepository pendingIntentRepository
    ) {
        mContext = context;
        mScheduleRepository = scheduleRepository;
        mPendingIntentRepository = pendingIntentRepository;

        mAlarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    public void scheduleEventNotificationAlarm(long triggerAtMillis, int pendingIntentRequestCode) {
        Intent intent = new Intent(mContext, EventNotificationAlarmReceiver.class);
        intent.putExtra(EventNotificationAlarmReceiver
                        .EVENT_NOTIFICATION_ALARM_PENDING_INTENT_ID_INTENT_KEY,
                pendingIntentRequestCode);

        //specifying a requestCode to make sure PendingIntents for different Events
        //and for [starts] and [starts soon] notifications don't overwrite each other
        //(even though EventNotificationAlarmReceiver uses the Event id from Intent's Extras
        //to decide which message to show)
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                mContext, pendingIntentRequestCode, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, alarmIntent);
    }

    public void cancelEventNotificationAlarmsBlocking(int eventId) {
        Intent intent = new Intent(mContext, EventNotificationAlarmReceiver.class);

        List<EventNotificationAlarmPendingIntent> pendingIntents = mPendingIntentRepository
                .getByEventIdBlocking(eventId);

        for (EventNotificationAlarmPendingIntent pendingIntent : pendingIntents) {
            int pendingIntentRequestCode = pendingIntent.getId();

            PendingIntent alarmIntent = PendingIntent.getBroadcast(
                    mContext, pendingIntentRequestCode, intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE);

            if (alarmIntent != null) {
                mAlarmMgr.cancel(alarmIntent);
            }
        }
    }

    public void cancelAllEventNotificationAlarmsBlocking() {
        List<Event> events = mScheduleRepository.getAllBlocking();

        for (Event event : events) {
            cancelEventNotificationAlarmsBlocking(event.getId());
        }
    }
}
