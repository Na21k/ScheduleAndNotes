package com.na21k.schedulenotes.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.na21k.schedulenotes.helpers.NotificationsHelper;

public class TestNotificationWorker extends Worker {

    public static final String EVENT_ID_INPUT_DATA_KEY = "eventIdInputDataKey";

    public TestNotificationWorker(@NonNull Context context,
                                  @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int noteId = getInputData().getInt(EVENT_ID_INPUT_DATA_KEY, 0);
        String requestId = getId().toString();

        NotificationsHelper.showTestNotification(getApplicationContext(),
                "Hello from Worker. Note: " + noteId, "Hello from Worker. Request: " + requestId, 0);

        return Result.success();
        //return Result.retry();
    }
}
