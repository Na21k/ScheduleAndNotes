package com.na21k.schedulenotes.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

public abstract class EventNotificationWorker extends NotificationWorkerBase {

    public static final String EVENT_ID_INPUT_DATA_KEY = "eventIdInputDataKey";

    public EventNotificationWorker(@NonNull Context context,
                                   @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
}
