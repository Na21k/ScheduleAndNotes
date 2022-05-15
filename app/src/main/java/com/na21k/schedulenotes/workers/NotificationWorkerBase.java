package com.na21k.schedulenotes.workers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.na21k.schedulenotes.Constants;

public abstract class NotificationWorkerBase extends Worker {

    public NotificationWorkerBase(@NonNull Context context,
                                  @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    protected boolean shouldNotify(String notificationTypePreferenceKey) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        return preferences.getBoolean(Constants.RECEIVE_NOTIFICATIONS_PREFERENCE_KEY, true)
                && preferences.getBoolean(notificationTypePreferenceKey, true);
    }
}
