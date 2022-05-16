package com.na21k.schedulenotes.data.database.Lists.Movies;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MoviesListItemDao {

    @Query("select count(*) from movies_list_items")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MoviesListItem moviesListItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<MoviesListItem> moviesListItems);

    @Query("select * from movies_list_items")
    LiveData<List<MoviesListItem>> getAll();

    @Query("select * from movies_list_items")
    List<MoviesListItem> getAllBlocking();

    @Query("select * from movies_list_items I where I.text like '%'||:search||'%'")
    LiveData<List<MoviesListItem>> search(String search);

    @Query("select * from movies_list_items I where I.id = :id")
    LiveData<MoviesListItem> getById(int id);

    @Query("select * from movies_list_items order by random() limit 1")
    MoviesListItem getRandomBlocking();

    @Delete
    void delete(MoviesListItem moviesListItem);

    @Update
    void update(MoviesListItem moviesListItem);

    @Query("delete from movies_list_items")
    void deleteAll();
}
