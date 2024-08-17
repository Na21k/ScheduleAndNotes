package com.na21k.schedulenotes.ui.notes.noteDetails;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.repositories.NotesRepository;

import java.util.List;

public class NoteDetailsViewModel extends AndroidViewModel {

    private final NotesRepository mNotesRepository;
    private final LiveData<List<Category>> mCategoriesLiveData;
    private LiveData<Note> mNoteLiveData;
    private List<Category> mCategories = null;
    private Note mNote;

    public NoteDetailsViewModel(@NonNull Application application) {
        super(application);

        mNotesRepository = new NotesRepository(application);
        CategoriesRepository categoriesRepository = new CategoriesRepository(application);

        mCategoriesLiveData = categoriesRepository.getAll();
    }

    public LiveData<Note> getNoteLiveData(int id) {
        return mNoteLiveData = mNotesRepository.getById(id);
    }

    public LiveData<List<Category>> getAllCategoriesLiveData() {
        return mCategoriesLiveData;
    }

    public void createNote(Note note) {
        mNotesRepository.add(note);
    }

    public void updateNote(Note note) {
        mNotesRepository.update(note);
    }

    public void deleteNote(Note note) {
        mNotesRepository.delete(note);
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
