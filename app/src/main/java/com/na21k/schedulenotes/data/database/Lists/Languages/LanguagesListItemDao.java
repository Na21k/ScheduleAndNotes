package com.na21k.schedulenotes.data.database.Lists.Languages;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LanguagesListItemDao {

    @Query("select count(*) from languages_list_items")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LanguagesListItem languagesListItem);

    @Query("select * from languages_list_items")
    LiveData<List<LanguagesListItem>> getAll();

    @Query("select * from languages_list_items I where I.id = :id")
    LiveData<LanguagesListItem> getById(int id);
}
