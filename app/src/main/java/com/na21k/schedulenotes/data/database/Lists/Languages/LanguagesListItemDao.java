package com.na21k.schedulenotes.data.database.Lists.Languages;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LanguagesListItemDao {

    @Query("select count(*) from languages_list_items")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(LanguagesListItem languagesListItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<LanguagesListItem> languagesListItems);

    @Query("select * from languages_list_items")
    LiveData<List<LanguagesListItem>> getAll();

    @Query("select * from languages_list_items")
    List<LanguagesListItem> getAllBlocking();

    @Query("select * from languages_list_items I where I.text like '%'||:search||'%'" +
            "or I.transcription like '%'||:search||'%'" +
            "or I.translation like '%'||:search||'%'" +
            "or I.explanation like '%'||:search||'%'" +
            "or I.usage_example_text like '%'||:search||'%'")
    LiveData<List<LanguagesListItem>> search(String search);

    @Query("select * from languages_list_items I where I.id = :id")
    LiveData<LanguagesListItem> getById(int id);

    @Query("select * from languages_list_items order by random() limit 1")
    LanguagesListItem getRandomBlocking();

    @Update
    void update(LanguagesListItem item);

    @Delete
    void delete(LanguagesListItem item);

    @Query("delete from languages_list_items where id = :id")
    void delete(int id);

    @Query("delete from languages_list_items")
    void deleteAll();
}
