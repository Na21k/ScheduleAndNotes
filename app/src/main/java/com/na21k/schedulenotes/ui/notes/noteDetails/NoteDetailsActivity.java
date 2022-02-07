package com.na21k.schedulenotes.ui.notes.noteDetails;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.databinding.ActivityNoteDetailsBinding;

public class NoteDetailsActivity extends AppCompatActivity implements Observer<Note> {

    private NoteDetailsViewModel mViewModel;
    private ActivityNoteDetailsBinding mBinding;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_details_menu, menu);

        if (!isEditing()) {
            menu.removeItem(R.id.menu_delete);
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveNote() {
        Editable titleEditable = mBinding.noteTitle.getText();
        Editable detailsEditable = mBinding.noteDetails.getText();

        if (titleEditable != null && !titleEditable.toString().isEmpty()) {
            Note note = new Note(0, titleEditable.toString(), detailsEditable.toString(), null);

            if (isEditing()) {
                mViewModel.updateCurrentNote(note);
            } else {
                mViewModel.createNote(note);
            }

            finish();
        } else {
            Toast.makeText(this, R.string.specify_note_title_toast, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteNote() {
        LiveData<Note> note = mViewModel.getCurrentNote();

        if (note != null) {
            note.removeObserver(this);
            mViewModel.deleteCurrentNote();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private boolean isEditing() {
        Bundle bundle = getIntent().getExtras();
        return bundle != null;
    }

    private void loadNoteFromDb(int noteId) {
        mViewModel.getNote(noteId).observe(this, this);
    }

    @Override
    public void onChanged(Note note) {
        mBinding.noteTitle.setText(note.getTitle());
        mBinding.noteDetails.setText(note.getDetails());
    }
}
