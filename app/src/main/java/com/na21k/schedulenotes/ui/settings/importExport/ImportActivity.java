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
import com.na21k.schedulenotes.helpers.WorkersHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class ImportActivity extends AppCompatActivity {

    private ImportActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        mViewModel = new ViewModelProvider(this).get(ImportActivityViewModel.class);

        startImport();
    }

    private void startImport() {
        ActivityResultLauncher<Intent> importActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(getApplicationContext(),
                                R.string.importing_from_backup_toast, Toast.LENGTH_SHORT).show();

                        Intent data = result.getData();

                        if (data != null) {
                            Uri uri = data.getData();
                            readFromFileInBackgroundThread(uri);
                        } else {
                            Toast.makeText(this, R.string.unexpected_error,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                R.string.importing_from_backup_failed_toast,
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
        );

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        importActivityResultLauncher.launch(intent);
    }

    private void readFromFileInBackgroundThread(Uri uri) {
        new Thread(() -> readFromFile(uri)).start();
    }

    private void readFromFile(Uri uri) {
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r");
            FileInputStream fileInputStream = new FileInputStream(pfd.getFileDescriptor());
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            List<Category> categories = (List<Category>) objectInputStream.readObject();
            List<Event> events = (List<Event>) objectInputStream.readObject();
            List<Note> notes = (List<Note>) objectInputStream.readObject();
            List<UserDefinedList> userDefinedLists = (List<UserDefinedList>) objectInputStream.readObject();
            List<UserDefinedListItem> userDefinedListItems = (List<UserDefinedListItem>) objectInputStream.readObject();
            List<MoviesListItem> moviesListItems = (List<MoviesListItem>) objectInputStream.readObject();
            List<MusicListItem> musicListItems = (List<MusicListItem>) objectInputStream.readObject();
            List<ShoppingListItem> shoppingListItems = (List<ShoppingListItem>) objectInputStream.readObject();
            List<LanguagesListItem> languagesListItems = (List<LanguagesListItem>) objectInputStream.readObject();

            WorkersHelper.cancelAllEventNotificationWorkers(this);
            mViewModel.clearDatabase();

            mViewModel.insertCategoriesBlocking(categories);
            mViewModel.insertEventsBlocking(events);
            mViewModel.insertNotesBlocking(notes);
            mViewModel.insertUserDefinedListsBlocking(userDefinedLists);
            mViewModel.insertUserDefinedListItemsBlocking(userDefinedListItems);
            mViewModel.insertMoviesListItemsBlocking(moviesListItems);
            mViewModel.insertMusicListItemsBlocking(musicListItems);
            mViewModel.insertShoppingListItemsBlocking(shoppingListItems);
            mViewModel.insertLanguagesListItemsBlocking(languagesListItems);

            for (int i = 0; i < languagesListItems.size(); i++) {
                List<LanguagesListItemAttachedImage> attachedImages =
                        (List<LanguagesListItemAttachedImage>) objectInputStream.readObject();
                mViewModel.insertLanguageListItemAttachedImagesBlocking(attachedImages);
            }

            objectInputStream.close();
            // Let the document provider know we're done by closing the stream.
            fileInputStream.close();
            pfd.close();

            runOnUiThread(() -> Toast.makeText(
                    this,
                    R.string.successfully_imported_from_backup_toast,
                    Toast.LENGTH_LONG).show());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(
                    this,
                    R.string.unexpected_error,
                    Toast.LENGTH_LONG).show());
        }

        finish();
    }
}