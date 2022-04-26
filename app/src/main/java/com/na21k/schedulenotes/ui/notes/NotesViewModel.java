package com.na21k.schedulenotes.ui.notes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.data.database.Notes.NoteDao;

import java.util.List;

public class NotesViewModel extends AndroidViewModel {

    private final NoteDao mNoteDao;
    private final LiveData<List<Note>> mAllNotes;
    private final LiveData<List<Category>> mAllCategories;
    private List<Note> mNotesCache = null;
    private List<Category> mCategoriesCache = null;

    public NotesViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mNoteDao = db.noteDao();
        mAllNotes = mNoteDao.getAll();
        mAllCategories = db.categoryDao().getAll();
    }

    public LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }

    public LiveData<List<Category>> getAllCategories() {
        return mAllCategories;
    }

    public void createNote(Note note) {
        new Thread(() -> mNoteDao.insert(note)).start();
    }

    public void updateNote(Note note) {
        new Thread(() -> mNoteDao.update(note)).start();
    }

    public void deleteNote(Note note) {
        new Thread(() -> mNoteDao.delete(note)).start();
    }

    public List<Note> getNotesCache() {
        return mNotesCache;
    }

    public void setNotesCache(List<Note> notesCache) {
        mNotesCache = notesCache;
    }

    public List<Category> getCategoriesCache() {
        return mCategoriesCache;
    }

    public void setCategoriesCache(List<Category> categoriesCache) {
        mCategoriesCache = categoriesCache;
    }
}
