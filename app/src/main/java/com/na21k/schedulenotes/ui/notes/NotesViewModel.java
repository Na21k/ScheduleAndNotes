package com.na21k.schedulenotes.ui.notes;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class NotesViewModel extends ViewModel {

    @NonNull
    private final MutableRepository<Note> mMutableNotesRepository;
    @NonNull
    private final CanSearchRepository<Note> mCanSearchNotesRepository;
    @NonNull
    private final LiveData<List<Note>> mAllNotes;
    @NonNull
    private final LiveData<List<Category>> mAllCategories;
    private List<Note> mNotesCache = null;
    private List<Category> mCategoriesCache = null;

    private NotesViewModel(
            @NonNull MutableRepository<Note> mutableNotesRepository,
            @NonNull CanSearchRepository<Note> canSearchNotesRepository,
            @NonNull CategoriesRepository categoriesRepository
    ) {
        super();

        mMutableNotesRepository = mutableNotesRepository;
        mCanSearchNotesRepository = canSearchNotesRepository;

        mAllNotes = mutableNotesRepository.getAll();
        mAllCategories = categoriesRepository.getAll();
    }

    @NonNull
    public LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }

    public LiveData<List<Note>> getNotesSearch(String searchQuery) {
        return mCanSearchNotesRepository.getSearch(searchQuery);
    }

    @NonNull
    public LiveData<List<Category>> getAllCategories() {
        return mAllCategories;
    }

    public void createNote(Note note) {
        mMutableNotesRepository.add(note);
    }

    public void updateNote(Note note) {
        mMutableNotesRepository.update(note);
    }

    public void deleteNote(Note note) {
        mMutableNotesRepository.delete(note);
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
        private final MutableRepository<Note> mMutableNotesRepository;
        @NonNull
        private final CanSearchRepository<Note> mCanSearchNotesRepository;
        @NonNull
        private final CategoriesRepository mCategoriesRepository;

        @Inject
        public Factory(
                @NonNull MutableRepository<Note> mutableNotesRepository,
                @NonNull CanSearchRepository<Note> canSearchNotesRepository,
                @NonNull CategoriesRepository categoriesRepository
        ) {
            mMutableNotesRepository = mutableNotesRepository;
            mCanSearchNotesRepository = canSearchNotesRepository;
            mCategoriesRepository = categoriesRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            NotesViewModel vm = new NotesViewModel(
                    mMutableNotesRepository, mCanSearchNotesRepository, mCategoriesRepository
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
