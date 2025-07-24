package com.na21k.schedulenotes.ui.notes.noteDetails;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.ScheduleNotesApplication;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.databinding.ActivityNoteDetailsBinding;
import com.na21k.schedulenotes.helpers.UiHelper;
import com.na21k.schedulenotes.ui.categories.categoryDetails.CategoryDetailsActivity;

import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

public class NoteDetailsActivity extends AppCompatActivity implements Observer<Note> {

    private NoteDetailsViewModel.Factory mViewModelFactory;
    private NoteDetailsViewModel mViewModel;
    private ActivityNoteDetailsBinding mBinding;
    private List<Category> mCategoriesLatest;
    private Integer mCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((ScheduleNotesApplication) getApplicationContext())
                .getAppComponent()
                .inject(this);
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, mViewModelFactory).get(NoteDetailsViewModel.class);
        mBinding = ActivityNoteDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar.appBar);

        handleWindowInsets();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(mViewModel.isEditing()
                ? R.string.title_edit_note
                : R.string.title_create_note);

        observeNote();
        observeCategories();
    }

    @Inject
    protected void initViewModelFactory(
            NoteDetailsViewModel.Factory.AssistedFactory viewModelFactoryAssistedFactory
    ) {
        int noteId = 0;
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            noteId = bundle.getInt(Constants.NOTE_ID_INTENT_KEY);
        }

        mViewModelFactory = viewModelFactoryAssistedFactory.create(noteId);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.note_details_menu, menu);

        if (!mViewModel.isEditing()) {
            menu.removeItem(R.id.menu_delete);
        }

        if (mCategoryId == null) {
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

    private void handleWindowInsets() {
        UiHelper.handleWindowInsets(getWindow(), mBinding.getRoot(),
                mBinding.container, mBinding.scrollView, null, true);
    }

    @Override
    public void onChanged(Note note) {
        if (note == null) {
            //ignore when not editing or during deletion
            return;
        }

        mBinding.noteTitle.setText(note.getTitle());
        mBinding.noteDetails.setText(note.getDetails());
        mCategoryId = note.getCategoryId();

        invalidateOptionsMenu();    //note category might have changed
    }

    private void observeNote() {
        mViewModel.getNote().observe(this, this);
    }

    private void observeCategories() {
        mViewModel.getCategories().observe(this,
                categories -> mCategoriesLatest = categories);
    }

    private void saveNote() {
        Editable titleEditable = mBinding.noteTitle.getText();
        Editable detailsEditable = mBinding.noteDetails.getText();

        if (titleEditable == null || titleEditable.toString().isEmpty()) {
            UiHelper.showSnackbar(mBinding.getRoot(), R.string.specify_note_title_snackbar);

            return;
        }

        Note note = new Note(0, titleEditable.toString(), detailsEditable.toString(), mCategoryId);

        mViewModel.saveNote(note);
        finish();
    }

    private void deleteNote() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_24);
        builder.setTitle(R.string.note_deletion_alert_title);
        builder.setMessage(R.string.note_deletion_alert_message);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            mViewModel.deleteNote();
            finish();
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }

    private void removeCategory() {
        mCategoryId = null;
        UiHelper.showSnackbar(mBinding.getRoot(), R.string.excluded_from_category_snackbar);

        invalidateOptionsMenu();    //hide the Exclude from category button
    }

    private void showCategorySelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_categories_24);
        builder.setTitle(R.string.pick_category_dialog_title);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.create_category_dialog_button, (dialog, which) -> {
            Intent intent = new Intent(this, CategoryDetailsActivity.class);
            startActivity(intent);
        });

        if (mCategoriesLatest != null) {
            mCategoriesLatest.sort(Comparator.comparing(Category::getTitle));

            ArrayAdapter<Category> adapter = new ArrayAdapter<>(NoteDetailsActivity.this,
                    android.R.layout.simple_list_item_1, mCategoriesLatest);
            builder.setAdapter(adapter, (dialog, which) -> {
                mCategoryId = mCategoriesLatest.get(which).getId();
                UiHelper.showSnackbar(mBinding.getRoot(), R.string.category_set_snackbar);
                invalidateOptionsMenu();    //show the Exclude from category button
            });

            builder.show();
        }
    }
}
