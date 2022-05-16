package com.na21k.schedulenotes.helpers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.na21k.schedulenotes.Constants;
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
        String name = "Events";
        String description = "Schedule events Notifications";
        int importance = NotificationManagerCompat.IMPORTANCE_HIGH;

        NotificationChannelCompat notificationChannel = new NotificationChannelCompat
                .Builder(EVENT_NOTIFICATIONS_CHANNEL_ID, importance)
                .setName(name)
                .setDescription(description)
                .setVibrationEnabled(true)
                .setLightsEnabled(true)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    public static void addMoviesNotificationChannel(Context context) {
        String name = "Movies List";
        int importance = NotificationManagerCompat.IMPORTANCE_LOW;

        NotificationChannelCompat notificationChannel = new NotificationChannelCompat
                .Builder(MOVIES_LIST_NOTIFICATIONS_CHANNEL_ID, importance)
                .setName(name)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    public static void addMusicNotificationChannel(Context context) {
        String name = "Music List";
        int importance = NotificationManagerCompat.IMPORTANCE_LOW;

        NotificationChannelCompat notificationChannel = new NotificationChannelCompat
                .Builder(MUSIC_LIST_NOTIFICATIONS_CHANNEL_ID, importance)
                .setName(name)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    public static void addLanguagesListNotificationChannel(Context context) {
        String name = "Vocabulary List";
        int importance = NotificationManagerCompat.IMPORTANCE_LOW;

        NotificationChannelCompat notificationChannel = new NotificationChannelCompat
                .Builder(LANGUAGES_LIST_NOTIFICATIONS_CHANNEL_ID, importance)
                .setName(name)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    public static void showEventNotification(Context context, String title, String text,
                                             int notificationId, int eventId) {
        PendingIntent intent = getEventNotificationPendingIntent(context, eventId);
        NotificationCompat.Builder builder = getBasicBuilder(context, title, text,
                EVENT_NOTIFICATIONS_CHANNEL_ID, R.drawable.ic_event_24)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setContentIntent(intent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }

    public static void showMovieNotification(Context context, String title, String text) {
        PendingIntent intent = getMovieNotificationPendingIntent(context);
        NotificationCompat.Builder builder = getBasicBuilder(context, title, text,
                MOVIES_LIST_NOTIFICATIONS_CHANNEL_ID, R.drawable.ic_clapperboard_24)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setContentIntent(intent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Constants.MOVIES_LIST_NOTIFICATION_ID, builder.build());
    }

    public static void showMusicNotification(Context context, String title, String text) {
        PendingIntent intent = getMusicNotificationPendingIntent(context);
        NotificationCompat.Builder builder = getBasicBuilder(context, title, text,
                MUSIC_LIST_NOTIFICATIONS_CHANNEL_ID, R.drawable.ic_music_note_24)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setContentIntent(intent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Constants.MUSIC_LIST_NOTIFICATION_ID, builder.build());
    }

    public static void showLanguagesListNotification(Context context, String title, String text, int wordId) {
        PendingIntent intent = getLanguagesListNotificationPendingIntent(context, wordId);
        NotificationCompat.Builder builder = getBasicBuilder(context, title, text,
                LANGUAGES_LIST_NOTIFICATIONS_CHANNEL_ID, R.drawable.ic_translate_24)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setContentIntent(intent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Constants.LANGUAGES_LIST_NOTIFICATION_ID, builder.build());
    }

    private static PendingIntent getEventNotificationPendingIntent(Context context, int eventId) {
        Intent intent = new Intent(context, EventDetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.EVENT_ID_INTENT_KEY, eventId);
        intent.putExtras(bundle);

        return PendingIntent.getActivity(context, eventId, intent,
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

        return PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE, bundle);
    }

    @NonNull
    private static NotificationCompat.Builder getBasicBuilder(Context context,
                                                              String title, String text,
                                                              String channelId,
                                                              int drawableResourceId) {
        return new NotificationCompat
                .Builder(context, channelId)
                .setSmallIcon(drawableResourceId)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setAutoCancel(true);
    }

    public static void cancelNotification(Context context, int notificationId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);
    }
}
