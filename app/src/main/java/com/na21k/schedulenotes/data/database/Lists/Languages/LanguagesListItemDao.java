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

    @Query("select * from languages_list_items")
    LiveData<List<LanguagesListItem>> getAll();

    @Query("select * from languages_list_items I where I.id = :id")
    LiveData<LanguagesListItem> getById(int id);

    @Update
    void update(LanguagesListItem item);

    @Delete
    void delete(LanguagesListItem item);

    @Query("delete from languages_list_items where id = :id")
    void delete(int id);
}
