package com.na21k.schedulenotes.ui.settings.importExport;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.ScheduleNotesApplication;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.data.database.Schedule.Event;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class ExportActivity extends AppCompatActivity {

    @Inject
    protected ExportActivityViewModel.Factory mViewModelFactory;
    private ExportActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((ScheduleNotesApplication) getApplicationContext())
                .getAppComponent()
                .inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_export);
        mViewModel = new ViewModelProvider(this, mViewModelFactory)
                .get(ExportActivityViewModel.class);

        startExport();
    }

    private void startExport() {
        Date today = new Date();
        String fileName = "scheduleAndNotesBackup_" + today.toString()
                .replace(' ', '_') + ".bak";

        ActivityResultLauncher<Intent> exportActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(getApplicationContext(),
                                R.string.backing_up_toast, Toast.LENGTH_SHORT).show();

                        Intent data = result.getData();

                        if (data != null) {
                            Uri uri = data.getData();
                            writeDataToFileInBackgroundThread(uri);
                        } else {
                            Toast.makeText(this, R.string.unexpected_error,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                R.string.backing_up_failed_toast, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
        );

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("*/bak");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        exportActivityResultLauncher.launch(intent);
    }

    private void writeDataToFileInBackgroundThread(Uri uri) {
        new Thread(() -> writeDataToFile(uri)).start();
    }

    private void writeDataToFile(Uri uri) {
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "wt");
            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            List<Category> categories = mViewModel.getAllCategoriesBlocking();
            List<Event> events = mViewModel.getAllEventsBlocking();
            List<Note> notes = mViewModel.getAllNotesBlocking();
            List<UserDefinedList> userDefinedLists = mViewModel.getAllUserDefinedListsBlocking();
            List<UserDefinedListItem> userDefinedListItems = mViewModel.getAllUserDefinedListItemsBlocking();
            List<MoviesListItem> moviesListItems = mViewModel.getAllMoviesListItemsBlocking();
            List<MusicListItem> musicListItems = mViewModel.getAllMusicListItemsBlocking();
            List<ShoppingListItem> shoppingListItems = mViewModel.getAllShoppingListItemsBlocking();
            List<LanguagesListItem> languagesListItems = mViewModel.getAllLanguagesListItemsBlocking();
            objectOutputStream.writeObject(categories);
            objectOutputStream.writeObject(events);
            objectOutputStream.writeObject(notes);
            objectOutputStream.writeObject(userDefinedLists);
            objectOutputStream.writeObject(userDefinedListItems);
            objectOutputStream.writeObject(moviesListItems);
            objectOutputStream.writeObject(musicListItems);
            objectOutputStream.writeObject(shoppingListItems);
            objectOutputStream.writeObject(languagesListItems);

            for (LanguagesListItem item : languagesListItems) {
                List<LanguagesListItemAttachedImage> attachedImages = mViewModel
                        .getAllLanguagesListItemAttachedImagesByListItemIdBlocking(item.getId());
                objectOutputStream.writeObject(attachedImages);
            }

            objectOutputStream.close();
            // Let the document provider know we're done by closing the stream.
            fileOutputStream.close();
            pfd.close();

            runOnUiThread(() -> Toast.makeText(this,
                    R.string.backup_created_successfully_toast,
                    Toast.LENGTH_LONG).show());
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this,
                    R.string.unexpected_error,
                    Toast.LENGTH_LONG).show());
        }

        finish();
    }
}
