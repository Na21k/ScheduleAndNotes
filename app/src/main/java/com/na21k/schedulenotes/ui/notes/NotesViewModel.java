package com.na21k.schedulenotes.ui.notes;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.repositories.NotesRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class NotesViewModel extends ViewModel {

    @NonNull
    private final NotesRepository mNotesRepository;
    @NonNull
    private final LiveData<List<Note>> mAllNotes;
    @NonNull
    private final LiveData<List<Category>> mAllCategories;
    private List<Note> mNotesCache = null;
    private List<Category> mCategoriesCache = null;

    private NotesViewModel(
            @NonNull NotesRepository notesRepository,
            @NonNull CategoriesRepository categoriesRepository
    ) {
        super();

        mNotesRepository = notesRepository;

        mAllNotes = notesRepository.getAll();
        mAllCategories = categoriesRepository.getAll();
    }

    @NonNull
    public LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }

    public LiveData<List<Note>> getNotesSearch(String searchQuery) {
        return mNotesRepository.getSearch(searchQuery);
    }

    @NonNull
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

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final NotesRepository mNotesRepository;
        @NonNull
        private final CategoriesRepository mCategoriesRepository;

        @Inject
        public Factory(
                @NonNull NotesRepository notesRepository,
                @NonNull CategoriesRepository categoriesRepository
        ) {
            mNotesRepository = notesRepository;
            mCategoriesRepository = categoriesRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            NotesViewModel vm = new NotesViewModel(mNotesRepository, mCategoriesRepository);
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
