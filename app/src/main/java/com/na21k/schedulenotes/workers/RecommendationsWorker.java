package com.na21k.schedulenotes.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.ScheduleNotesApplication;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.helpers.NotificationsHelper;
import com.na21k.schedulenotes.repositories.CanProvideRandomRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListRepository;

import java.util.StringJoiner;

import javax.inject.Inject;

public class RecommendationsWorker extends Worker {

    @Inject
    protected CanProvideRandomRepository<MoviesListItem> mCanProvideRandomMoviesListRepository;
    @Inject
    protected CanProvideRandomRepository<MusicListItem> mCanProvideRandomMusicListRepository;
    private final CanProvideRandomRepository<LanguagesListItem> mCanProvideRandomLanguagesListRepository;

    public RecommendationsWorker(@NonNull Context context,
                                 @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        mCanProvideRandomLanguagesListRepository = new LanguagesListRepository(context);
    }

    private void inject() {
        ((ScheduleNotesApplication) getApplicationContext())
                .getAppComponent()
                .inject(this);
    }

    @NonNull
    @Override
    public Result doWork() {
        inject();

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

        MoviesListItem item = mCanProvideRandomMoviesListRepository.getRandomBlocking();

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

        MusicListItem item = mCanProvideRandomMusicListRepository.getRandomBlocking();

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

        LanguagesListItem item = mCanProvideRandomLanguagesListRepository.getRandomBlocking();

        if (item == null) {
            return;
        }

        String title = item.getText();
        String text = getLanguagesListNotificationText(item);

        NotificationsHelper.showLanguagesListNotification(context, title, text, item.getId());
    }

    private static String getLanguagesListNotificationText(LanguagesListItem item) {
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

        return textJoiner.toString();
    }
}
