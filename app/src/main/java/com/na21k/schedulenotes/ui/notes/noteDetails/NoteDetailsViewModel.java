package com.na21k.schedulenotes.ui.notes.noteDetails;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.data.database.Notes.NoteDao;

import java.util.List;

public class NoteDetailsViewModel extends AndroidViewModel {

    private final NoteDao mNoteDao;
    private final LiveData<List<Category>> mCategories;
    private List<Category> mCategoriesCache = null;
    private LiveData<Note> mNote;
    private int mNoteId;

    public NoteDetailsViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mNoteDao = db.noteDao();
        mCategories = db.categoryDao().getAll();
    }

    public LiveData<Note> getNote(int id) {
        if (mNoteId != id) {
            mNote = mNoteDao.getById(id);
            mNoteId = id;
        }

        return mNote;
    }

    public LiveData<List<Category>> getAllCategories() {
        return mCategories;
    }

    public void createNote(Note note) {
        new Thread(() -> mNoteDao.insert(note)).start();
    }

    public void deleteCurrentNote() {
        new Thread(() -> mNoteDao.delete(mNoteId)).start();
    }

    public void updateCurrentNote(Note note) {
        note.setId(mNoteId);
        new Thread(() -> mNoteDao.update(note)).start();
    }

    @Nullable
    public LiveData<Note> getCurrentNote() {
        return mNote;
    }

    public List<Category> getCategoriesCache() {
        return mCategoriesCache;
    }

    public void setCategoriesCache(List<Category> categoriesCache) {
        mCategoriesCache = categoriesCache;
    }
}
