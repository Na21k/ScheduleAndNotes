package com.na21k.schedulenotes.data.database.Lists.Music;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MusicListItemDao {

    @Query("select count(*) from music_list_items")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MusicListItem musicListItem);

    @Query("select * from music_list_items")
    LiveData<List<MusicListItem>> getAll();

    @Query("select * from music_list_items I where I.text like '%'||:search||'%'")
    LiveData<List<MusicListItem>> search(String search);

    @Query("select * from music_list_items I where I.id = :id")
    LiveData<MusicListItem> getById(int id);

    @Query("select * from music_list_items order by random() limit 1")
    MusicListItem getRandomBlocking();

    @Update
    void update(MusicListItem musicListItem);

    @Delete
    void delete(MusicListItem musicListItem);
}
