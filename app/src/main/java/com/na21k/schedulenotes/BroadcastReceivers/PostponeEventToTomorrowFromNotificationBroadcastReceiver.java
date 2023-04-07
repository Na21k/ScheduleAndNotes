package com.na21k.schedulenotes.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.helpers.NotificationsHelper;

public class PostponeEventToTomorrowFromNotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        int eventId = b.getInt(Constants.EVENT_ID_INTENT_KEY);

        Toast.makeText(context, "Notification ID is Event ID: " + eventId, Toast.LENGTH_LONG).show();

        NotificationsHelper.cancelNotification(context, eventId);
    }
}
