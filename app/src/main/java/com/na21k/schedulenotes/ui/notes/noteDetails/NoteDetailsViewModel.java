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
    private final LiveData<List<Category>> mCategoriesLiveData;
    private LiveData<Note> mNoteLiveData;
    private List<Category> mCategories = null;
    private Note mNote;

    public NoteDetailsViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mNoteDao = db.noteDao();
        mCategoriesLiveData = db.categoryDao().getAll();
    }

    public LiveData<Note> getNoteLiveData(int id) {
        return mNoteLiveData = mNoteDao.getById(id);
    }

    public LiveData<List<Category>> getAllCategoriesLiveData() {
        return mCategoriesLiveData;
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

    @Nullable
    public LiveData<Note> getCurrentNoteLiveData() {
        return mNoteLiveData;
    }

    public List<Category> getCategoriesCache() {
        return mCategories;
    }

    public void setCategoriesCache(List<Category> categoriesCache) {
        mCategories = categoriesCache;
    }

    public Note getNoteCache() {
        return mNote;
    }

    public Note setNoteCache(@NonNull Note note) {
        return mNote = note;
    }
}
