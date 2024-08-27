package com.na21k.schedulenotes.data.database.Notes;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.na21k.schedulenotes.data.database.BaseDao;

import java.util.List;

@Dao
public interface NoteDao extends BaseDao<Note> {

    @Override
    @Query("select * from notes")
    LiveData<List<Note>> getAll();

    @Override
    @Query("select * from notes")
    List<Note> getAllBlocking();

    @Override
    @Query("select * from notes N where N.id = :entityId")
    LiveData<Note> getById(int entityId);

    @Override
    @Query("select * from notes N where N.id = :entityId")
    Note getByIdBlocking(int entityId);

    @Query("select * from notes N where N.title like '%'||:search||'%'" +
            "or N.details like '%'||:search||'%'" +
            "or N.category_id in" +
            "(select C.id from categories C where C.title like '%'||:search||'%')")
    LiveData<List<Note>> search(String search);

    @Override
    @Query("delete from notes where id = :entityId")
    void delete(int entityId);

    @Query("delete from notes")
    void deleteAll();
}
