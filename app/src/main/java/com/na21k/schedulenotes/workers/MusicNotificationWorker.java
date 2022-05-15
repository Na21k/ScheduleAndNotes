package com.na21k.schedulenotes.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.helpers.NotificationsHelper;

public class MusicNotificationWorker extends NotificationWorkerBase {

    public MusicNotificationWorker(@NonNull Context context,
                                   @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        boolean shouldNotify = shouldNotify(
                Constants.RECEIVE_MUSIC_LIST_NOTIFICATIONS_PREFERENCE_KEY);

        if (!shouldNotify) {
            return Result.success();
        }

        Context context = getApplicationContext();
        MusicListItem item = AppDatabase.getInstance(context)
                .musicListItemDao().getRandomBlocking();

        if (item == null) {
            return Result.success();
        }

        String text = context.getResources().getString(R.string.music_notification_text);
        NotificationsHelper.showMusicNotification(context, item.getText(), text);

        return Result.success();
    }
}
