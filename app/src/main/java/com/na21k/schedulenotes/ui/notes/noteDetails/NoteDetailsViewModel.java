package com.na21k.schedulenotes.ui.notes.noteDetails;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.repositories.NotesRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class NoteDetailsViewModel extends ViewModel {

    @NonNull
    private final NotesRepository mNotesRepository;
    @NonNull
    private final LiveData<List<Category>> mCategoriesLiveData;
    @Nullable
    private LiveData<Note> mNoteLiveData;
    private List<Category> mCategories = null;
    private Note mNote;

    private NoteDetailsViewModel(
            @NonNull NotesRepository notesRepository,
            @NonNull CategoriesRepository categoriesRepository
    ) {
        super();

        mNotesRepository = notesRepository;

        mCategoriesLiveData = categoriesRepository.getAll();
    }

    public LiveData<Note> getNoteLiveData(int id) {
        return mNoteLiveData = mNotesRepository.getById(id);
    }

    @NonNull
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
            NoteDetailsViewModel vm = new NoteDetailsViewModel(
                    mNotesRepository, mCategoriesRepository
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
