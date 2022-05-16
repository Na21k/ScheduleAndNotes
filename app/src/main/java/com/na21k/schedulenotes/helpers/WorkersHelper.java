package com.na21k.schedulenotes.helpers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.data.database.Notifications.ScheduledNotification;
import com.na21k.schedulenotes.data.database.Notifications.ScheduledNotificationDao;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.workers.EventNotificationWorker;
import com.na21k.schedulenotes.workers.EventStartsNotificationWorker;
import com.na21k.schedulenotes.workers.EventStartsSoonNotificationWorker;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class WorkersHelper {

    public static WorkRequest getOneTimeWorkRequest(
            @NonNull Class<? extends ListenableWorker> workerClass, Data inputData,
            long initialDelayMillis, String tag) {
        OneTimeWorkRequest.Builder requestBuilder = new OneTimeWorkRequest
                .Builder(workerClass)
                .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS);

        if (inputData != null) {
            requestBuilder.setInputData(inputData);
        }
        if (tag != null) {
            requestBuilder.addTag(tag);
        }

        return requestBuilder.build();
    }

    public static void cancelRequest(String requestId, Context context) {
        if (requestId != null) {
            UUID uuid = UUID.fromString(requestId);
            WorkManager.getInstance(context).cancelWorkById(uuid);
        }
    }

    public static void scheduleEventNotificationsBlocking(
            @NonNull Event event, @NonNull ScheduledNotificationDao notificationDao,
            Context context) {
        Date starts = event.getDateTimeStarts();
        Date startsSoon = DateTimeHelper.addMinutes(starts, -30);
        Date now = new Date();
        long startsDelayMillis = starts.getTime() - now.getTime();
        long startsSoonDelayMillis = startsSoon.getTime() - now.getTime();

        Data inputData = new Data.Builder()
                .putInt(EventNotificationWorker.EVENT_ID_INPUT_DATA_KEY, event.getId())
                .build();

        WorkRequest startsRequest = WorkersHelper.getOneTimeWorkRequest(
                EventStartsNotificationWorker.class, inputData, startsDelayMillis,
                Constants.EVENT_NOTIFICATION_WORKER_TAG);
        WorkRequest startsSoonRequest = WorkersHelper.getOneTimeWorkRequest(
                EventStartsSoonNotificationWorker.class, inputData, startsSoonDelayMillis,
                Constants.EVENT_NOTIFICATION_WORKER_TAG);

        String startsRequestId = startsRequest.getId().toString();
        String startsSoonRequestId = startsSoonRequest.getId().toString();

        notificationDao.insert(new ScheduledNotification(0, startsRequestId));
        notificationDao.insert(new ScheduledNotification(0, startsSoonRequestId));

        WorkManager.getInstance(context).enqueue(startsRequest);
        WorkManager.getInstance(context).enqueue(startsSoonRequest);

        event.setLastStartsNotificationRequestId(startsRequestId);
        event.setLastStartsSoonNotificationRequestId(startsSoonRequestId);
    }

    public static void cancelAllEventNotificationWorkers(Context context) {
        WorkManager.getInstance(context)
                .cancelAllWorkByTag(Constants.EVENT_NOTIFICATION_WORKER_TAG);
    }
}
