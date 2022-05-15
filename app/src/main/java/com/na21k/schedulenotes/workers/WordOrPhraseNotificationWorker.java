package com.na21k.schedulenotes.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.helpers.NotificationsHelper;

public class WordOrPhraseNotificationWorker extends NotificationWorkerBase {

    public WordOrPhraseNotificationWorker(@NonNull Context context,
                                          @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        boolean shouldNotify = shouldNotify(
                Constants.RECEIVE_LANGUAGES_LIST_NOTIFICATIONS_PREFERENCE_KEY);

        if (!shouldNotify) {
            return Result.success();
        }

        Context context = getApplicationContext();
        LanguagesListItem item = AppDatabase.getInstance(context)
                .languagesListItemDao().getRandomBlocking();

        if (item == null) {
            return Result.success();
        }

        String text = "";

        String transcription = item.getTranscription();
        String translation = item.getTranslation();
        String explanation = item.getExplanation();
        String usageExamples = item.getUsageExampleText();

        if (!transcription.isEmpty()) {
            text += transcription + '\n';
        }
        if (!translation.isEmpty()) {
            text += translation + '\n';
        }
        if (!explanation.isEmpty()) {
            text += explanation + '\n';
        }
        if (!usageExamples.isEmpty()) {
            text += usageExamples;
        }

        NotificationsHelper.showLanguagesListNotification(context,
                item.getText(), text, item.getId());

        return Result.success();
    }
}
