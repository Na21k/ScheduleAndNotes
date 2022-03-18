package com.na21k.schedulenotes.data.database.Lists.Movies;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MoviesListItemDao {

    @Query("select count(*) from movies_list_items")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MoviesListItem moviesListItem);

    @Query("select * from movies_list_items")
    LiveData<List<MoviesListItem>> getAll();

    @Query("select * from movies_list_items I where I.id = :id")
    LiveData<MoviesListItem> getById(int id);

    @Delete
    void delete(MoviesListItem moviesListItem);
}
