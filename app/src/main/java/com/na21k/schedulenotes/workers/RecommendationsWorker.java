package com.na21k.schedulenotes.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.helpers.NotificationsHelper;

import java.util.StringJoiner;

public class RecommendationsWorker extends Worker {

    public RecommendationsWorker(@NonNull Context context,
                                 @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        showMoviesListNotification(context);
        showMusicListNotification(context);
        showLanguagesListNotification(context);

        return Result.success();
    }

    private void showMoviesListNotification(Context context) {
        boolean shouldNotify = NotificationsHelper.shouldNotify(
                context, Constants.RECEIVE_MOVIES_LIST_NOTIFICATIONS_PREFERENCE_KEY);

        if (!shouldNotify) {
            return;
        }

        MoviesListItem item = AppDatabase.getInstance(context)
                .moviesListItemDao().getRandomBlocking();

        if (item == null) {
            return;
        }

        String text = context.getResources().getString(R.string.movie_notification_text);
        NotificationsHelper.showMovieNotification(context, item.getText(), text);
    }

    private void showMusicListNotification(Context context) {
        boolean shouldNotify = NotificationsHelper.shouldNotify(
                context, Constants.RECEIVE_MUSIC_LIST_NOTIFICATIONS_PREFERENCE_KEY);

        if (!shouldNotify) {
            return;
        }

        MusicListItem item = AppDatabase.getInstance(context)
                .musicListItemDao().getRandomBlocking();

        if (item == null) {
            return;
        }

        String text = context.getResources().getString(R.string.music_notification_text);
        NotificationsHelper.showMusicNotification(context, item.getText(), text);
    }

    private void showLanguagesListNotification(Context context) {
        boolean shouldNotify = NotificationsHelper.shouldNotify(
                context, Constants.RECEIVE_LANGUAGES_LIST_NOTIFICATIONS_PREFERENCE_KEY);

        if (!shouldNotify) {
            return;
        }

        LanguagesListItem item = AppDatabase.getInstance(context)
                .languagesListItemDao().getRandomBlocking();

        if (item == null) {
            return;
        }

        String transcription = item.getTranscription();
        String translation = item.getTranslation();
        String explanation = item.getExplanation();
        String usageExamples = item.getUsageExampleText();

        StringJoiner textJoiner = new StringJoiner("\n");

        if (!transcription.isEmpty()) {
            textJoiner.add(transcription);
        }

        if (!translation.isEmpty()) {
            textJoiner.add(translation);
        }

        if (!explanation.isEmpty()) {
            textJoiner.add(explanation);
        }

        if (!usageExamples.isEmpty()) {
            textJoiner.add(usageExamples);
        }

        String title = item.getText();
        String text = textJoiner.toString();

        NotificationsHelper.showLanguagesListNotification(context, title, text, item.getId());
    }
}
