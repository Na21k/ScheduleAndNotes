package com.na21k.schedulenotes.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.data.database.Notes.NoteDao;

import java.util.List;

public class NotesRepository extends MutableRepository<Note, Void>
        implements CanListRepository<Note>, CanSearchRepository<Note>, CanClearRepository {

    private final NoteDao mNoteDao = db.noteDao();
    private final LiveData<List<Note>> mAllItems = mNoteDao.getAll();

    public NotesRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<Note> getById(int id) {
        return mNoteDao.getById(id);
    }

    @Override
    public LiveData<List<Note>> getAll() {
        return mAllItems;
    }

    @Override
    public List<Note> getAllBlocking() {
        return mNoteDao.getAllBlocking();
    }

    @Override
    public LiveData<List<Note>> getSearch(String query) {
        return mNoteDao.search(query);
    }

    @Override
    public Task<Void> add(Note item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mNoteDao.insert(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> add(List<Note> items) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mNoteDao.insert(items);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public void addBlocking(List<Note> items) {
        mNoteDao.insert(items);
    }

    @Override
    public Task<Void> update(Note item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mNoteDao.update(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> delete(Note item) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mNoteDao.delete(item);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public void deleteAllBlocking() {
        mNoteDao.deleteAll();
    }
}
