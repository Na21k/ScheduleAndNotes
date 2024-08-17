package com.na21k.schedulenotes.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.helpers.DateTimeHelper;
import com.na21k.schedulenotes.helpers.EventsHelper;
import com.na21k.schedulenotes.helpers.NotificationsHelper;
import com.na21k.schedulenotes.repositories.ScheduleRepository;

import java.util.Date;

public class PostponeEventByOneDayFromNotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        int eventId = b.getInt(Constants.EVENT_ID_INTENT_KEY);

        NotificationsHelper.cancelNotification(context, eventId);
        PendingResult pendingResult = goAsync();

        new Thread(() -> {
            Event event = new ScheduleRepository(context).getByIdBlocking(eventId);
            Date newStarts = DateTimeHelper.addDays(event.getDateTimeStarts(), 1);
            Date newStartsDateOnly = DateTimeHelper.truncateToDateOnly(newStarts);

            EventsHelper.postponeToBlocking(event, newStartsDateOnly, context);

            Handler mainHandler = new Handler(context.getMainLooper());
            mainHandler.post(() -> {
                Toast.makeText(context, newStarts.toString(), Toast.LENGTH_LONG).show();

                pendingResult.finish();
            });
        }).start();
    }
}
