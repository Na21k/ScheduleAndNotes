package com.na21k.schedulenotes.data.database;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.DeleteColumn;
import androidx.room.DeleteTable;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.na21k.schedulenotes.Constants;
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
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.database.Schedule.EventDao;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent;
import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntentDao;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

@Database(entities = {Category.class, Note.class, Event.class,
        EventNotificationAlarmPendingIntent.class,
        UserDefinedList.class, UserDefinedListItem.class,
        ShoppingListItem.class, MoviesListItem.class, MusicListItem.class,
        LanguagesListItem.class, LanguagesListItemAttachedImage.class},
        version = 10,
        autoMigrations = {
                @AutoMigration(from = 7, to = 8, spec = AppDatabase.AutoMigration_7_8.class),
                @AutoMigration(from = 8, to = 9),
                @AutoMigration(from = 9, to = 10)})
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract CategoryDao categoryDao();

    public abstract NoteDao noteDao();

    public abstract EventDao eventDao();

    public abstract EventNotificationAlarmPendingIntentDao eventNotificationAlarmPendingIntentDao();

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
                            .addMigrations(new Migration_10_11(context))
                            .fallbackToDestructiveMigrationFrom(1, 2, 3, 4, 5, 6)
                            .build();
                }
            }
        }

        return appDatabaseInstance;
    }

    //now using AlarmManager for event notifications, this table is not needed anymore
    @DeleteTable(tableName = "scheduled_notifications")
    @DeleteColumn(tableName = "events", columnName = "last_starts_notification_request_id")
    @DeleteColumn(tableName = "events", columnName = "last_starts_soon_notification_request_id")
    public static class AutoMigration_7_8 implements AutoMigrationSpec {
    }

    public static class Migration_10_11 extends Migration {

        private final String mFilesDir;

        public Migration_10_11(Context context) {
            super(10, 11);
            mFilesDir = context.getFilesDir().getAbsolutePath();
        }

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            Cursor cur = db.query("select * from languages_list_items_attached_images");
            final int imageIdColumnId = cur.getColumnIndexOrThrow("id");
            final int languagesListItemIdColumnId = cur.getColumnIndexOrThrow("languages_list_item_id");
            final int bitmapDataColumnId = cur.getColumnIndexOrThrow("binary_data");

            cur.moveToFirst();

            //TODO: test whether isAfterLast() == true when getCount() == 0
            while (!cur.isAfterLast()) {
                int imageId = cur.getInt(imageIdColumnId);
                int languagesListItemId = cur.getInt(languagesListItemIdColumnId);
                byte[] bitmapData = cur.getBlob(bitmapDataColumnId);

                //TODO: inject the DB and have the images folder path injected?
                //will need to append only the item id and image id
                String fileLocation = String.format(
                        Locale.US,
                        "%s/%s/%d/%d",
                        mFilesDir,
                        Constants.LANGUAGES_LIST_ATTACHED_IMAGES_LOCATION_RELATIVE,
                        languagesListItemId,
                        imageId
                );

                Bitmap img = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);

                try (FileOutputStream out = new FileOutputStream(fileLocation)) {
                    img.compress(Bitmap.CompressFormat.JPEG, 20, out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                cur.moveToNext();
            }

            cur.close();
            db.execSQL("drop table languages_list_items_attached_images");
        }
    }
}
