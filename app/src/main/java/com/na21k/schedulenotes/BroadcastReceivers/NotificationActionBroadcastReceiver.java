package com.na21k.schedulenotes.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.helpers.DateTimeHelper;
import com.na21k.schedulenotes.helpers.EventsHelper;
import com.na21k.schedulenotes.helpers.NotificationsHelper;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.ScheduleRepository;
import com.na21k.schedulenotes.repositories.lists.languages.LanguagesListRepository;

import java.util.Date;

public class NotificationActionBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();

        if (action == null) return;

        switch (action) {
            case "com.na21k.schedulenotes.POSTPONE_EVENT_BY_ONE_DAY_FROM_NOTIFICATION":
                if (bundle == null) return;

                int eventId = bundle.getInt(Constants.EVENT_ID_INTENT_KEY);
                postponeEventAsync(context, eventId);

                break;
            case "com.na21k.schedulenotes.ARCHIVE_LANGUAGES_LIST_ITEM_FROM_NOTIFICATION":
                if (bundle == null) return;

                int languagesListItemId = bundle.getInt(Constants.LANGUAGES_LIST_ITEM_ID_INTENT_KEY);
                archiveLanguagesListItemAsync(context, languagesListItemId);

                break;
            default:
                showToast(context, this.getClass().getSimpleName() + ": unknown intent action");
        }
    }

    private void postponeEventAsync(Context context, int eventId) {
        NotificationsHelper.cancelNotification(context, eventId);
        PendingResult pendingResult = goAsync();

        new Thread(() -> {
            Event event = new ScheduleRepository(context).getByIdBlocking(eventId);
            Date newStarts = DateTimeHelper.addDays(event.getDateTimeStarts(), 1);
            Date newStartsDateOnly = DateTimeHelper.truncateToDateOnly(newStarts);

            EventsHelper.postponeToBlocking(event, newStartsDateOnly, context);

            showToast(context, newStarts.toString());
            pendingResult.finish();
        }).start();
    }

    private void archiveLanguagesListItemAsync(Context context, int itemId) {
        NotificationsHelper.cancelNotification(context, Constants.LANGUAGES_LIST_NOTIFICATION_ID);
        PendingResult pendingResult = goAsync();

        new Thread(() -> {
            MutableRepository<LanguagesListItem> repository = new LanguagesListRepository(context);
            LanguagesListItem item = repository.getByIdBlocking(itemId);
            item.setArchived(true);
            repository.update(item)
                    .addOnCompleteListener(task -> pendingResult.finish());
        }).start();
    }

    private void showToast(Context context, String text) {
        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(() -> Toast.makeText(context, text, Toast.LENGTH_LONG).show());
    }
}
