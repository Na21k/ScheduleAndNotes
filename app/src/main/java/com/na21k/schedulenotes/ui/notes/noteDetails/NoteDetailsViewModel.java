package com.na21k.schedulenotes.ui.notes.noteDetails;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.repositories.NotesRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

public class NoteDetailsViewModel extends ViewModel {

    @NonNull
    private final NotesRepository mNotesRepository;
    private final int mNoteId;
    @NonNull
    private final LiveData<Note> mNote;
    @NonNull
    private final LiveData<List<Category>> mCategories;

    private NoteDetailsViewModel(
            @NonNull NotesRepository notesRepository,
            int noteId,
            @NonNull CategoriesRepository categoriesRepository
    ) {
        super();

        mNotesRepository = notesRepository;
        mNoteId = noteId;

        mNote = notesRepository.getById(noteId);
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

        if (isEditing()) mNotesRepository.update(note);
        else mNotesRepository.add(note);
    }

    public void deleteNote() {
        mNotesRepository.delete(mNoteId);
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final NotesRepository mNotesRepository;
        @NonNull
        private final CategoriesRepository mCategoriesRepository;
        private final int mNoteId;

        @AssistedInject
        public Factory(
                @NonNull NotesRepository notesRepository,
                @Assisted int noteId,
                @NonNull CategoriesRepository categoriesRepository
        ) {
            mNotesRepository = notesRepository;
            mCategoriesRepository = categoriesRepository;
            mNoteId = noteId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            NoteDetailsViewModel vm = new NoteDetailsViewModel(
                    mNotesRepository, mNoteId, mCategoriesRepository
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
