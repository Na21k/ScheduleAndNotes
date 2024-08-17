package com.na21k.schedulenotes.ui.notes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.repositories.NotesRepository;

import java.util.List;

public class NotesViewModel extends AndroidViewModel {

    private final NotesRepository mNotesRepository;
    private final LiveData<List<Note>> mAllNotes;
    private final LiveData<List<Category>> mAllCategories;
    private List<Note> mNotesCache = null;
    private List<Category> mCategoriesCache = null;

    public NotesViewModel(@NonNull Application application) {
        super(application);

        mNotesRepository = new NotesRepository(application);
        CategoriesRepository categoriesRepository = new CategoriesRepository(application);

        mAllNotes = mNotesRepository.getAll();
        mAllCategories = categoriesRepository.getAll();
    }

    public LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }

    public LiveData<List<Note>> getNotesSearch(String searchQuery) {
        return mNotesRepository.getSearch(searchQuery);
    }

    public LiveData<List<Category>> getAllCategories() {
        return mAllCategories;
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
