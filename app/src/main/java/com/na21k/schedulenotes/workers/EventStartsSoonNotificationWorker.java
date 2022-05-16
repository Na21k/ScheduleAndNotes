package com.na21k.schedulenotes.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Notifications.ScheduledNotification;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.helpers.NotificationsHelper;

public class EventStartsSoonNotificationWorker extends EventNotificationWorker {

    public EventStartsSoonNotificationWorker(@NonNull Context context,
                                             @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        boolean shouldNotify = shouldNotify(
                Constants.RECEIVE_SCHEDULE_NOTIFICATIONS_PREFERENCE_KEY);

        if (!shouldNotify) {
            return Result.success();
        }

        Context context = getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(context);

        int eventId = getInputData().getInt(EVENT_ID_INPUT_DATA_KEY, 0);
        Event event = db.eventDao().getByIdBlocking(eventId);

        if (event == null) {
            return Result.failure();
        }

        String requestId = getId().toString();
        ScheduledNotification notification = db.scheduledNotificationDao()
                .getByRequestIdBlocking(requestId);

        if (notification == null) {
            return Result.failure();
        }

        int notificationId = notification.getId();

        String title = context.getResources()
                .getString(R.string.event_starts_soon_title, event.getTitle());
        NotificationsHelper.showEventNotification(context, title, event.getDetails(),
                notificationId, eventId);

        return Result.success();
    }
}
