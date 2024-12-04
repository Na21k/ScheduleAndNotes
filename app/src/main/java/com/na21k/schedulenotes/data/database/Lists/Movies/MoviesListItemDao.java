package com.na21k.schedulenotes.data.database.Lists.Movies;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.CanClearDao;
import com.na21k.schedulenotes.data.database.CanProvideRandomDao;
import com.na21k.schedulenotes.data.database.CanSearchDao;

import java.util.List;

@Dao
public interface MoviesListItemDao extends BaseDao<MoviesListItem>,
        CanSearchDao<MoviesListItem>, CanClearDao, CanProvideRandomDao<MoviesListItem> {

    @Query("select count(*) from movies_list_items")
    int getCount();

    @Override
    @Query("select * from movies_list_items")
    LiveData<List<MoviesListItem>> getAll();

    @Override
    @Query("select * from movies_list_items")
    List<MoviesListItem> getAllBlocking();

    @Override
    @Query("select * from movies_list_items I where I.id = :entityId")
    LiveData<MoviesListItem> getById(int entityId);

    @Override
    @Query("select * from movies_list_items I where I.id = :entityId")
    MoviesListItem getByIdBlocking(int entityId);

    @Override
    @Query("select * from movies_list_items order by random() limit 1")
    MoviesListItem getRandomBlocking();

    @Override
    @Query("select * from movies_list_items I where I.text like '%'||:query||'%'")
    LiveData<List<MoviesListItem>> search(String query);

    @Override
    @Query("delete from movies_list_items where id = :entityId")
    void delete(int entityId);

    @Override
    @Query("delete from movies_list_items")
    void deleteAll();
}
