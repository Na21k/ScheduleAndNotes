package com.na21k.schedulenotes.di.modules.repositories;

import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.NotesRepository;
import com.na21k.schedulenotes.repositories.Repository;

import dagger.Binds;
import dagger.Module;

@Module
public interface NoteRepositoriesModule {

    @Binds
    Repository<Note> bindNotesRepository(NotesRepository notesRepository);

    @Binds
    MutableRepository<Note> bindNotesRepositoryMutable(NotesRepository notesRepository);

    @Binds
    CanSearchRepository<Note> bindCanSearchNotesRepository(NotesRepository notesRepository);

    @Binds
    CanClearRepository<Note> bindCanClearNotesRepository(NotesRepository notesRepository);
}
