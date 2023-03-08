package com.na21k.schedulenotes.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Categories.CategoryDao;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImageDao;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemDao;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItemDao;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItemDao;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItemDao;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListDao;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItemDao;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.data.database.Notes.NoteDao;
import com.na21k.schedulenotes.data.database.Notifications.ScheduledNotification;
import com.na21k.schedulenotes.data.database.Notifications.ScheduledNotificationDao;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventDao;

@Database(entities = {Category.class, Note.class, Event.class,
        ScheduledNotification.class,
        UserDefinedList.class, UserDefinedListItem.class,
        ShoppingListItem.class, MoviesListItem.class, MusicListItem.class,
        LanguagesListItem.class, LanguagesListItemAttachedImage.class},
        version = 7)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract CategoryDao categoryDao();

    public abstract NoteDao noteDao();

    public abstract EventDao eventDao();

    public abstract ScheduledNotificationDao scheduledNotificationDao();

    public abstract UserDefinedListDao userDefinedListDao();

    public abstract UserDefinedListItemDao userDefinedListItemDao();

    public abstract ShoppingListItemDao shoppingListItemDao();

    public abstract MoviesListItemDao moviesListItemDao();

    public abstract MusicListItemDao musicListItemDao();

    public abstract LanguagesListItemDao languagesListItemDao();

    public abstract LanguagesListItemAttachedImageDao languagesListItemAttachedImageDao();

    private static volatile AppDatabase appDatabaseInstance;

    public static AppDatabase getInstance(final Context context) {
        if (appDatabaseInstance == null) {
            synchronized (AppDatabase.class) {
                if (appDatabaseInstance == null) {
                    appDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .fallbackToDestructiveMigrationFrom(1, 2, 3, 4, 5, 6).build();
                }
            }
        }

        return appDatabaseInstance;
    }
}
