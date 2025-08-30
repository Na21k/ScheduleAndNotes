package com.na21k.schedulenotes.ui.notes.noteDetails;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

public class NoteDetailsViewModel extends ViewModel {

    @NonNull
    private final MutableRepository<Note> mMutableNotesRepository;
    private final int mNoteId;
    @NonNull
    private final LiveData<Note> mNote;
    @NonNull
    private final LiveData<List<Category>> mCategories;

    private NoteDetailsViewModel(
            @NonNull MutableRepository<Note> mutableNotesRepository,
            int noteId,
            @NonNull CategoriesRepository categoriesRepository
    ) {
        super();

        mMutableNotesRepository = mutableNotesRepository;
        mNoteId = noteId;

        mNote = mutableNotesRepository.getById(noteId);
        mCategories = categoriesRepository.getAll();
    }

    public boolean isEditing() {
        return mNoteId != 0;
    }

    @NonNull
    public LiveData<Note> getNote() {
        return mNote;
    }

    @NonNull
    public LiveData<List<Category>> getCategories() {
        return mCategories;
    }

    public void saveNote(@NonNull Note note) {
        note.setId(mNoteId);

        if (isEditing()) mMutableNotesRepository.update(note);
        else mMutableNotesRepository.add(note);
    }

    public void deleteNote() {
        mMutableNotesRepository.delete(mNoteId);
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final MutableRepository<Note> mMutableNotesRepository;
        @NonNull
        private final CategoriesRepository mCategoriesRepository;
        private final int mNoteId;

        @AssistedInject
        public Factory(
                @NonNull MutableRepository<Note> mutableNotesRepository,
                @Assisted int noteId,
                @NonNull CategoriesRepository categoriesRepository
        ) {
            mMutableNotesRepository = mutableNotesRepository;
            mCategoriesRepository = categoriesRepository;
            mNoteId = noteId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            NoteDetailsViewModel vm = new NoteDetailsViewModel(
                    mMutableNotesRepository, mNoteId, mCategoriesRepository
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }

        @dagger.assisted.AssistedFactory
        public interface AssistedFactory {

            Factory create(int noteId);
        }
    }
}
