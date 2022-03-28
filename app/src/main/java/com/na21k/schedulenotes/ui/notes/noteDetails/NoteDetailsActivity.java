package com.na21k.schedulenotes.ui.notes.noteDetails;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.databinding.ActivityNoteDetailsBinding;

import java.util.Comparator;

public class NoteDetailsActivity extends AppCompatActivity implements Observer<Note> {

    private NoteDetailsViewModel mViewModel;
    private ActivityNoteDetailsBinding mBinding;
    private Integer mCurrentNotesCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(NoteDetailsViewModel.class);
        mBinding = ActivityNoteDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (isEditing()) {
            setTitle(R.string.title_edit_note);

            Bundle bundle = getIntent().getExtras();
            int noteId = bundle.getInt(Constants.NOTE_ID_INTENT_KEY);
            loadNoteFromDb(noteId);
        } else {
            setTitle(R.string.title_create_note);
        }
    }

    @Override
    public void onChanged(Note note) {
        mBinding.noteTitle.setText(note.getTitle());
        mBinding.noteDetails.setText(note.getDetails());
        mCurrentNotesCategoryId = note.getCategoryId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_details_menu, menu);

        if (!isEditing()) {
            menu.removeItem(R.id.menu_delete);
        }

        if (mCurrentNotesCategoryId == null) {
            menu.removeItem(R.id.menu_remove_category);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                saveNote();
                break;
            case R.id.menu_cancel:
                finish();
                break;
            case R.id.menu_delete:
                deleteNote();
                break;
            case R.id.menu_set_category:
                showCategorySelection();
                break;
            case R.id.menu_remove_category:
                removeCategory();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void saveNote() {
        Editable titleEditable = mBinding.noteTitle.getText();
        Editable detailsEditable = mBinding.noteDetails.getText();

        if (titleEditable != null && !titleEditable.toString().isEmpty()) {
            Note note = new Note(0, titleEditable.toString(), detailsEditable.toString(),
                    mCurrentNotesCategoryId);

            if (isEditing()) {
                mViewModel.updateCurrentNote(note);
            } else {
                mViewModel.createNote(note);
            }

            finish();
        } else {
            showSnackbar(R.string.specify_note_title_snackbar);
        }
    }

    private void deleteNote() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_24);
        builder.setTitle(R.string.note_deletion_alert_title);
        builder.setMessage(R.string.note_deletion_alert_message);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            LiveData<Note> note = mViewModel.getCurrentNote();

            if (note != null) {
                note.removeObserver(this);
                mViewModel.deleteCurrentNote();
                finish();
            }
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }

    private void removeCategory() {
        mCurrentNotesCategoryId = null;
        showSnackbar(R.string.excluded_from_category_snackbar);

        invalidateOptionsMenu();    //hide the Exclude from category button
    }

    private void showCategorySelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_categories_24);
        builder.setTitle(R.string.pick_category_dialog_title);
        builder.setNegativeButton(R.string.cancel, null);

        mViewModel.getAllCategories().observe(this, categories -> {
            categories.sort(Comparator.comparing(Category::getTitle));

            ArrayAdapter<Category> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, categories);
            builder.setAdapter(adapter, (dialog, which) -> {
                mCurrentNotesCategoryId = categories.get(which).getId();
                showSnackbar(R.string.category_set_snackbar);
                invalidateOptionsMenu();    //show the Exclude from category button
            });

            builder.show();
        });
    }

    private boolean isEditing() {
        Bundle bundle = getIntent().getExtras();
        return bundle != null;
    }

    private void loadNoteFromDb(int noteId) {
        mViewModel.getNote(noteId).observe(this, this);
    }

    private void showSnackbar(@StringRes int stringResourceId) {
        Snackbar.make(mBinding.activityNoteDetailsRoot, stringResourceId, 3000).show();
    }
}
