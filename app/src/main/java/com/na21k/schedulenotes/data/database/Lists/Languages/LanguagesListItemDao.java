package com.na21k.schedulenotes.data.database.Lists.Languages;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.CanClearDao;
import com.na21k.schedulenotes.data.database.CanSearchDao;

import java.util.List;

@Dao
public interface LanguagesListItemDao extends BaseDao<LanguagesListItem>,
        CanSearchDao<LanguagesListItem>, CanClearDao {

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

    @Query("select * from languages_list_items where is_archived = 0")
    LiveData<List<LanguagesListItem>> getUnarchived();

    @Query("select * from languages_list_items where is_archived = 1")
    LiveData<List<LanguagesListItem>> getArchived();

    @Query("select (select count(*) from languages_list_items where is_archived = 1) = 0")
    boolean isArchiveEmptyBlocking();

    @Override
    @Query("select * from languages_list_items I where I.is_archived = 0 and " +
            "(I.text like '%'||:query||'%'" +
            "or I.transcription like '%'||:query||'%'" +
            "or I.translation like '%'||:query||'%'" +
            "or I.explanation like '%'||:query||'%'" +
            "or I.usage_example_text like '%'||:query||'%')")
    LiveData<List<LanguagesListItem>> search(String query);

    @Override
    @Query("delete from languages_list_items where id = :entityId")
    void delete(int entityId);

    @Override
    @Query("delete from languages_list_items")
    void deleteAll();
}
