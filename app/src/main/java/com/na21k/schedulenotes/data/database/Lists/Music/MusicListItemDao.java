package com.na21k.schedulenotes.data.database.Lists.Music;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.na21k.schedulenotes.data.database.BaseDao;

import java.util.List;

@Dao
public interface MusicListItemDao extends BaseDao<MusicListItem> {

    @Query("select count(*) from music_list_items")
    int getCount();

    @Override
    @Query("select * from music_list_items")
    LiveData<List<MusicListItem>> getAll();

    @Override
    @Query("select * from music_list_items")
    List<MusicListItem> getAllBlocking();

    @Override
    @Query("select * from music_list_items I where I.id = :entityId")
    LiveData<MusicListItem> getById(int entityId);

    @Override
    @Query("select * from music_list_items I where I.id = :entityId")
    MusicListItem getByIdBlocking(int entityId);

    @Query("select * from music_list_items order by random() limit 1")
    MusicListItem getRandomBlocking();

    @Query("select * from music_list_items I where I.text like '%'||:search||'%'")
    LiveData<List<MusicListItem>> search(String search);

    @Override
    @Query("delete from music_list_items where id = :entityId")
    void delete(int entityId);

    @Query("delete from music_list_items")
    void deleteAll();
}
