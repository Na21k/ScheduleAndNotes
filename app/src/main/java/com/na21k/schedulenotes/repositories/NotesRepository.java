package com.na21k.schedulenotes.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.data.database.Notes.NoteDao;

import java.util.List;

import javax.inject.Inject;

public class NotesRepository extends MutableRepository<Note>
        implements CanSearchRepository<Note>, CanClearRepository<Note> {

    private final NoteDao mNoteDao = db.noteDao();

    @Inject
    public NotesRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<List<Note>> getSearch(String query) {
        return mNoteDao.search(query);
    }

    @Override
    public void clearBlocking() {
        mNoteDao.deleteAll();
    }

    @Override
    protected BaseDao<Note> getDao() {
        return mNoteDao;
    }
}
