package com.na21k.schedulenotes;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.na21k.schedulenotes.data.models.ColorSet;
import com.na21k.schedulenotes.helpers.DateTimeHelper;

import java.util.Date;

public class Constants {

    public static final String CATEGORY_ID_INTENT_KEY = "categoryId";
    public static final String NOTE_ID_INTENT_KEY = "noteId";
    public static final String EVENT_ID_INTENT_KEY = "eventId";
    public static final String LANGUAGES_LIST_ITEM_ID_INTENT_KEY = "languagesListItemId";
    public static final String LIST_ID_INTENT_KEY = "listId";
    public static final String LIST_TITLE_INTENT_KEY = "listTitle";
    public static final String SELECTED_TIME_MILLIS_INTENT_KEY = "selectedTimeMillis";
    public static final String IS_OPEN_FROM_ARCHIVE_INTENT_KEY = "isOpenFromArchive";
    public static final ColorSet DEFAULT_COLOR_SET = ColorSet.GRAY;
    @IntRange(from = 1)
    public static final int DEFAULT_SNACKBAR_TIMEOUT_MILLIS = 3000;
    @IntRange(from = 1)
    public static final int UNDO_DELETE_TIMEOUT_MILLIS = 7000;
    public static final int ATTACHED_IMAGES_COUNT_LIMIT = 5;
    public static final String OPEN_IMAGE_TMP_FILE_NAME = "openImageTmpFile";
    public static final String RECEIVE_NOTIFICATIONS_PREFERENCE_KEY = "receive_notifications";
    public static final String RECEIVE_SCHEDULE_NOTIFICATIONS_PREFERENCE_KEY = "receive_schedule_notifications";
    public static final String RECEIVE_MOVIES_LIST_NOTIFICATIONS_PREFERENCE_KEY = "receive_movies_list_notifications";
    public static final String RECEIVE_MUSIC_LIST_NOTIFICATIONS_PREFERENCE_KEY = "receive_music_list_notifications";
    public static final String RECEIVE_LANGUAGES_LIST_NOTIFICATIONS_PREFERENCE_KEY = "receive_languages_list_notifications";
    public static final String LANGUAGES_LIST_SORTING_ORDER_PREFERENCE_KEY = "languages_list_sorting_order";

    //positive notification id's are reserved for schedule events notifications
    public static final int EVENT_NOTIFICATIONS_GROUP_SUMMARY_NOTIFICATION_ID = -1;
    public static final int MOVIES_LIST_NOTIFICATION_ID = -2;
    public static final int MUSIC_LIST_NOTIFICATION_ID = -3;
    public static final int LANGUAGES_LIST_NOTIFICATION_ID = -4;
    public static final String EVENT_NOTIFICATIONS_GROUP_ID = "eventNotificationsGroupId";
    public static final String RECOMMENDATIONS_WORKER_TAG = "recommendationsWorkerTag";

    @NonNull
    public static Date getRecommendationsTime() {
        return DateTimeHelper.addHours(DateTimeHelper.truncateToDateOnly(new Date()), 15);
    }
}
