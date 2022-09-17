package com.na21k.schedulenotes.ui.notes.noteDetails;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.databinding.ActivityNoteDetailsBinding;
import com.na21k.schedulenotes.helpers.UiHelper;
import com.na21k.schedulenotes.ui.categories.categoryDetails.CategoryDetailsActivity;

import java.util.Comparator;
import java.util.List;

public class NoteDetailsActivity extends AppCompatActivity implements Observer<Note> {

    private NoteDetailsViewModel mViewModel;
    private ActivityNoteDetailsBinding mBinding;
    private Integer mCurrentNotesCategoryId;
    private int mMostRecentBottomInset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(NoteDetailsViewModel.class);
        mBinding = ActivityNoteDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar.appBar);

        makeNavBarLookNice();

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

        loadCategoriesFromDb();
    }

    @Override
    public void onChanged(Note note) {
        mBinding.noteTitle.setText(note.getTitle());
        mBinding.noteDetails.setText(note.getDetails());
        mCurrentNotesCategoryId = note.getCategoryId();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
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

    private void makeNavBarLookNice() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.getRoot(), (v, insets) -> {
            Insets i = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            mBinding.activityNoteDetailsRoot.setPadding(i.left, i.top, i.right, 0);
            mBinding.scrollView.setPadding(0, 0, 0, i.bottom);

            mMostRecentBottomInset = i.bottom;

            return WindowInsetsCompat.CONSUMED;
        });
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
            UiHelper.showSnackbar(this, mBinding.getRoot(),
                    R.string.specify_note_title_snackbar, mMostRecentBottomInset);
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
        UiHelper.showSnackbar(this, mBinding.getRoot(),
                R.string.excluded_from_category_snackbar, mMostRecentBottomInset);

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

        List<Category> categories = mViewModel.getCategoriesCache();

        if (categories != null) {
            categories.sort(Comparator.comparing(Category::getTitle));

            ArrayAdapter<Category> adapter = new ArrayAdapter<>(NoteDetailsActivity.this,
                    android.R.layout.simple_list_item_1, categories);
            builder.setAdapter(adapter, (dialog, which) -> {
                mCurrentNotesCategoryId = categories.get(which).getId();
                UiHelper.showSnackbar(this, mBinding.getRoot(),
                        R.string.category_set_snackbar, mMostRecentBottomInset);
                invalidateOptionsMenu();    //show the Exclude from category button
            });

            builder.show();
        }
    }

    private boolean isEditing() {
        Bundle bundle = getIntent().getExtras();
        return bundle != null;
    }

    private void loadNoteFromDb(int noteId) {
        mViewModel.getNote(noteId).observe(this, this);
    }

    private void loadCategoriesFromDb() {
        mViewModel.getAllCategories().observe(this,
                categories -> mViewModel.setCategoriesCache(categories));
    }
}
