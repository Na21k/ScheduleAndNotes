package com.na21k.schedulenotes.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntentDao;
import com.na21k.schedulenotes.helpers.EventsHelper;

import java.util.List;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            return;
        }

        PendingResult pendingResult = goAsync();

        new Thread(() -> {
            scheduleEventNotificationsBlocking(context);
            pendingResult.finish();
        }).start();
    }

    private void scheduleEventNotificationsBlocking(Context context) {
        EventNotificationAlarmPendingIntentDao dao = AppDatabase.getInstance(context)
                .eventNotificationAlarmPendingIntentDao();

        //only pending intents for notifications that haven't been sent yet are stored
        List<EventNotificationAlarmPendingIntent> pendingIntents = dao.getAllBlocking();

        for (EventNotificationAlarmPendingIntent pendingIntent : pendingIntents) {
            EventsHelper.scheduleEventNotificationBlocking(pendingIntent, context);
        }
    }
}
