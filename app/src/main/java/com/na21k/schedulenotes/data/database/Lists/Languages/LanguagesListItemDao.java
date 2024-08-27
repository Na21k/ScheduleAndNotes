package com.na21k.schedulenotes.data.database.Lists.Languages;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.na21k.schedulenotes.data.database.BaseDao;

import java.util.List;

@Dao
public interface LanguagesListItemDao extends BaseDao<LanguagesListItem> {

    @Query("select count(*) from languages_list_items")
    int getCount();

    @Override
    @Query("select * from languages_list_items")
    LiveData<List<LanguagesListItem>> getAll();

    @Override
    @Query("select * from languages_list_items")
    List<LanguagesListItem> getAllBlocking();

    @Override
    @Query("select * from languages_list_items I where I.id = :entityId")
    LiveData<LanguagesListItem> getById(int entityId);

    @Override
    @Query("select * from languages_list_items I where I.id = :entityId")
    LanguagesListItem getByIdBlocking(int entityId);

    @Query("select * from languages_list_items order by random() limit 1")
    LanguagesListItem getRandomBlocking();

    @Query("select * from languages_list_items I where I.text like '%'||:search||'%'" +
            "or I.transcription like '%'||:search||'%'" +
            "or I.translation like '%'||:search||'%'" +
            "or I.explanation like '%'||:search||'%'" +
            "or I.usage_example_text like '%'||:search||'%'")
    LiveData<List<LanguagesListItem>> search(String search);

    @Override
    @Query("delete from languages_list_items where id = :entityId")
    void delete(int entityId);

    @Query("delete from languages_list_items")
    void deleteAll();
}
