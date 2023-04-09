package com.na21k.schedulenotes.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.na21k.schedulenotes.helpers.EventsHelper;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            return;
        }

        PendingResult pendingResult = goAsync();

        new Thread(() -> {
            EventsHelper.ensureEventNotificationsScheduledBlocking(context);
            pendingResult.finish();
        }).start();
    }
}
