package com.na21k.schedulenotes;

import androidx.annotation.IntRange;

import com.na21k.schedulenotes.data.models.ColorSet;

public class Constants {

    public static final String CATEGORY_ID_INTENT_KEY = "categoryId";
    public static final String NOTE_ID_INTENT_KEY = "noteId";
    public static final String EVENT_ID_INTENT_KEY = "eventId";
    public static final String LANGUAGES_LIST_ITEM_ID_INTENT_KEY = "languagesListItemId";
    public static final String LIST_ID_INTENT_KEY = "listId";
    public static final String LIST_TITLE_INTENT_KEY = "listTitle";
    public static final String SELECTED_TIME_MILLIS_INTENT_KEY = "currentTimeMillis";
    public static final ColorSet DEFAULT_COLOR_SET = ColorSet.GRAY;
    @IntRange(from = 1)
    public static final int UNDO_DELETE_TIMEOUT_MILLIS = 7000;
    public static final int ATTACHED_IMAGES_COUNT_LIMIT = 5;
    public static final String OPEN_IMAGE_TMP_FILE_NAME = "openImageTmpFile";
}
