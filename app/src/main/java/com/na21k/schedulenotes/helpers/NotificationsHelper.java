package com.na21k.schedulenotes.helpers;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.preference.PreferenceManager;

import com.na21k.schedulenotes.BroadcastReceivers.NotificationActionBroadcastReceiver;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.MainActivity;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.WordOrPhraseDetailsActivity;
import com.na21k.schedulenotes.ui.lists.movies.MoviesListActivity;
import com.na21k.schedulenotes.ui.lists.music.MusicListActivity;
import com.na21k.schedulenotes.ui.schedule.eventDetails.EventDetailsActivity;

public class NotificationsHelper {
    private static final String EVENT_NOTIFICATIONS_CHANNEL_ID = "eventNotificationsChannelId";
    private static final String MOVIES_LIST_NOTIFICATIONS_CHANNEL_ID = "moviesListNotificationsChannelId";
    private static final String MUSIC_LIST_NOTIFICATIONS_CHANNEL_ID = "musicListNotificationsChannelId";
    private static final String LANGUAGES_LIST_NOTIFICATIONS_CHANNEL_ID = "languagesListNotificationsChannelId";

    public static void addEventsNotificationChannel(Context context) {
        Resources resources = context.getResources();

        String name = resources.getString(R.string.schedule_events_notification_channel_name);
        String description = resources.getString(R.string.schedule_events_notification_channel_description);
        int importance = NotificationManagerCompat.IMPORTANCE_HIGH;

        NotificationChannelCompat notificationChannel = new NotificationChannelCompat
                .Builder(EVENT_NOTIFICATIONS_CHANNEL_ID, importance)
                .setName(name)
                .setDescription(description)
                .setVibrationEnabled(true)
                .setLightsEnabled(true)
                .setLightColor(context.getColor(R.color.event_notifications_LED))
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    public static void addMoviesNotificationChannel(Context context) {
        Resources resources = context.getResources();

        String name = resources.getString(R.string.movies_list_notification_channel_name);
        int importance = NotificationManagerCompat.IMPORTANCE_LOW;

        NotificationChannelCompat notificationChannel = new NotificationChannelCompat
                .Builder(MOVIES_LIST_NOTIFICATIONS_CHANNEL_ID, importance)
                .setName(name)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    public static void addMusicNotificationChannel(Context context) {
        Resources resources = context.getResources();

        String name = resources.getString(R.string.music_list_notification_channel_name);
        int importance = NotificationManagerCompat.IMPORTANCE_LOW;

        NotificationChannelCompat notificationChannel = new NotificationChannelCompat
                .Builder(MUSIC_LIST_NOTIFICATIONS_CHANNEL_ID, importance)
                .setName(name)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    public static void addLanguagesListNotificationChannel(Context context) {
        Resources resources = context.getResources();

        String name = resources.getString(R.string.languages_list_notification_channel_name);
        int importance = NotificationManagerCompat.IMPORTANCE_LOW;

        NotificationChannelCompat notificationChannel = new NotificationChannelCompat
                .Builder(LANGUAGES_LIST_NOTIFICATIONS_CHANNEL_ID, importance)
                .setName(name)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    public static void showEventNotification(Context context, String title, String text,
                                             int eventId) {
        PendingIntent intent = getEventNotificationPendingIntent(context, eventId);
        NotificationCompat.Builder builder = getBasicBuilder(context, title, text,
                EVENT_NOTIFICATIONS_CHANNEL_ID, R.drawable.ic_event_24)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setGroup(Constants.EVENT_NOTIFICATIONS_GROUP_ID)
                .setContentIntent(intent);

        PendingIntent postponeByOneDayIntent = getPostponeEventByOneDayPendingIntent(
                context, eventId);
        NotificationCompat.Action postponeToTomorrowAction = new NotificationCompat
                .Action(R.drawable.ic_schedule_24,
                context.getString(R.string.postpone_event_by_one_day),
                postponeByOneDayIntent);
        builder.addAction(postponeToTomorrowAction);

        notifyIfNotificationsPermissionGranted(context, eventId, builder.build());
        showEventNotificationsGroupSummary(context);
    }

    private static void showEventNotificationsGroupSummary(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        String title = context.getString(R.string.event_notifications_group_notification_title);

        NotificationCompat.Builder builder = getBasicBuilder(context, title, null,
                EVENT_NOTIFICATIONS_CHANNEL_ID, R.drawable.ic_event_24)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setGroup(Constants.EVENT_NOTIFICATIONS_GROUP_ID)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setGroupSummary(true)
                .setContentIntent(pendingIntent);

        notifyIfNotificationsPermissionGranted(context,
                Constants.EVENT_NOTIFICATIONS_GROUP_SUMMARY_NOTIFICATION_ID, builder.build());
    }

    public static void showMovieNotification(Context context, String title, String text) {
        PendingIntent intent = getMovieNotificationPendingIntent(context);
        NotificationCompat.Builder builder = getBasicBuilder(context, title, text,
                MOVIES_LIST_NOTIFICATIONS_CHANNEL_ID, R.drawable.ic_clapperboard_24)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setContentIntent(intent);

        notifyIfNotificationsPermissionGranted(context, Constants.MOVIES_LIST_NOTIFICATION_ID, builder.build());
    }

    public static void showMusicNotification(Context context, String title, String text) {
        PendingIntent intent = getMusicNotificationPendingIntent(context);
        NotificationCompat.Builder builder = getBasicBuilder(context, title, text,
                MUSIC_LIST_NOTIFICATIONS_CHANNEL_ID, R.drawable.ic_music_note_24)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setContentIntent(intent);

        notifyIfNotificationsPermissionGranted(context, Constants.MUSIC_LIST_NOTIFICATION_ID, builder.build());
    }

    public static void showLanguagesListNotification(Context context, String title, String text, int wordId) {
        PendingIntent intent = getLanguagesListNotificationPendingIntent(context, wordId);
        NotificationCompat.Builder builder = getBasicBuilder(context, title, text,
                LANGUAGES_LIST_NOTIFICATIONS_CHANNEL_ID, R.drawable.ic_translate_24)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setContentIntent(intent);

        PendingIntent archiveIntent = getArchiveLanguagesListItemPendingIntent(context, wordId);
        NotificationCompat.Action postponeToTomorrowAction = new NotificationCompat
                .Action(R.drawable.ic_archive_24,
                context.getString(R.string.archive),
                archiveIntent);
        builder.addAction(postponeToTomorrowAction);

        notifyIfNotificationsPermissionGranted(context, Constants.LANGUAGES_LIST_NOTIFICATION_ID, builder.build());
    }

    private static PendingIntent getEventNotificationPendingIntent(Context context, int eventId) {
        Intent intent = new Intent(context, EventDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.EVENT_ID_INTENT_KEY, eventId);
        intent.putExtras(bundle);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context)
                .addParentStack(EventDetailsActivity.class)
                .addNextIntent(intent);

        return taskStackBuilder.getPendingIntent(eventId,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getPostponeEventByOneDayPendingIntent(Context context,
                                                                       int eventId) {
        Intent intent = new Intent(context, NotificationActionBroadcastReceiver.class);
        intent.setAction("com.na21k.schedulenotes.POSTPONE_EVENT_BY_ONE_DAY_FROM_NOTIFICATION");
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.EVENT_ID_INTENT_KEY, eventId);
        intent.putExtras(bundle);

        return PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getMovieNotificationPendingIntent(Context context) {
        Intent intent = new Intent(context, MoviesListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE, null);
    }

    private static PendingIntent getMusicNotificationPendingIntent(Context context) {
        Intent intent = new Intent(context, MusicListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE, null);
    }

    private static PendingIntent getLanguagesListNotificationPendingIntent(Context context,
                                                                           int wordId) {
        Intent intent = new Intent(context, WordOrPhraseDetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LANGUAGES_LIST_ITEM_ID_INTENT_KEY, wordId);
        intent.putExtras(bundle);

        return PendingIntent.getActivity(context, wordId, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getArchiveLanguagesListItemPendingIntent(Context context,
                                                                          int wordId) {
        Intent intent = new Intent(context, NotificationActionBroadcastReceiver.class);
        intent.setAction("com.na21k.schedulenotes.ARCHIVE_LANGUAGES_LIST_ITEM_FROM_NOTIFICATION");
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LANGUAGES_LIST_ITEM_ID_INTENT_KEY, wordId);
        intent.putExtras(bundle);

        return PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @NonNull
    private static NotificationCompat.Builder getBasicBuilder(Context context,
                                                              String title, String text,
                                                              String channelId,
                                                              int drawableResourceId) {
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(context, channelId)
                .setSmallIcon(drawableResourceId)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setAutoCancel(true);

        if (text != null && !text.isEmpty()) {
            builder.setContentText(text);
        }

        return builder;
    }

    public static void cancelNotification(Context context, int notificationId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);
    }

    public static boolean shouldNotify(Context context, String notificationTypePreferenceKey) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getBoolean(Constants.RECEIVE_NOTIFICATIONS_PREFERENCE_KEY, true)
                && preferences.getBoolean(notificationTypePreferenceKey, true);
    }

    private static void notifyIfNotificationsPermissionGranted(@NonNull Context context,
                                                               int notificationId,
                                                               @NonNull Notification notification) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationId, notification);
        }
    }
}
