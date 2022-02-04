package com.na21k.schedulenotes.data.database.Notes;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("select count(*) from notes")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Note note);

    @Query("select * from notes")
    LiveData<List<Note>> getAll();

    @Query("select * from notes N where N.title like '%'||:search||'%'" +
            "or N.details like '%'||:search||'%'")
    LiveData<List<Note>> search(String search);
}
