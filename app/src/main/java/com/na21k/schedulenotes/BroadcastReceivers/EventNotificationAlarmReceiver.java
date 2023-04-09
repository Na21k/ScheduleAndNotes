package com.na21k.schedulenotes.BroadcastReceivers;

import static com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent.EventNotificationType;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventDao;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntentDao;
import com.na21k.schedulenotes.helpers.NotificationsHelper;

public class EventNotificationAlarmReceiver extends BroadcastReceiver {

    public static final String EVENT_NOTIFICATION_ALARM_PENDING_INTENT_ID_INTENT_KEY =
            "eventNotificationAlarmPendingIntentId";
    private int mEventNotificationAlarmPendingIntentId;
    private EventDao mEventDao;
    private EventNotificationAlarmPendingIntentDao mEventNotificationAlarmPendingIntentDao;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!shouldNotify(context)) {
            return;
        }

        mEventNotificationAlarmPendingIntentId = intent
                .getIntExtra(EVENT_NOTIFICATION_ALARM_PENDING_INTENT_ID_INTENT_KEY, 0);

        AppDatabase db = AppDatabase.getInstance(context);
        mEventDao = db.eventDao();
        mEventNotificationAlarmPendingIntentDao = db.eventNotificationAlarmPendingIntentDao();

        PendingResult pendingResult = goAsync();

        new Thread(() -> {
            EventNotificationAlarmPendingIntent pendingIntent =
                    mEventNotificationAlarmPendingIntentDao
                            .getByIdBlocking(mEventNotificationAlarmPendingIntentId);

            if (pendingIntent != null) {
                int eventId = pendingIntent.getEventId();
                EventNotificationType notificationType = pendingIntent.getNotificationType();

                Event event = mEventDao.getByIdBlocking(eventId);

                String title;

                switch (notificationType) {
                    case EventStarted:
                        title = context.getResources().getString(
                                R.string.event_started_title, event.getTitle());
                        break;
                    case EventStartsSoon:
                        title = context.getResources().getString(
                                R.string.event_starts_soon_title, event.getTitle());
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Unexpected notification type: " + notificationType.name());
                }

                String text = event.getDetails();

                NotificationsHelper.showEventNotification(context, title, text, eventId);
                //only pending intents for notifications that haven't been sent yet are stored
                mEventNotificationAlarmPendingIntentDao.delete(pendingIntent);
            }

            pendingResult.finish();
        }).start();
    }

    //TODO: move to NotificationsHelper (same code is present in NotificationWorkerBase)
    private boolean shouldNotify(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getBoolean(
                Constants.RECEIVE_NOTIFICATIONS_PREFERENCE_KEY, true)
                && preferences.getBoolean(
                Constants.RECEIVE_SCHEDULE_NOTIFICATIONS_PREFERENCE_KEY, true);
    }
}
