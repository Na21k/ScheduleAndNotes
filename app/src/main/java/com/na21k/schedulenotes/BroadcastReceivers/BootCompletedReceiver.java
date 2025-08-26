package com.na21k.schedulenotes.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.na21k.schedulenotes.ScheduleNotesApplication;
import com.na21k.schedulenotes.helpers.EventsHelper2;

import java.util.Objects;

public class BootCompletedReceiver extends BroadcastReceiver {

    protected EventsHelper2 mEventsHelper;

    private void inject(Context context) {
        ((ScheduleNotesApplication) context.getApplicationContext())
                .getAppComponent()
                .inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        inject(context);

        if (!Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            return;
        }

        PendingResult pendingResult = goAsync();

        new Thread(() -> {
            mEventsHelper.ensureEventNotificationsScheduledBlocking();
            pendingResult.finish();
        }).start();
    }
}
