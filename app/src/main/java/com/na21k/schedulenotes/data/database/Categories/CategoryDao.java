package com.na21k.schedulenotes.data.database.Categories;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.CanClearDao;
import com.na21k.schedulenotes.data.database.CanSearchDao;

import java.util.List;

@Dao
public interface CategoryDao extends BaseDao<Category>, CanSearchDao<Category>, CanClearDao {

    @Override
    @Query("select * from categories")
    LiveData<List<Category>> getAll();

    @Override
    @Query("select * from categories")
    List<Category> getAllBlocking();

    @Override
    @Query("select * from categories C where C.id = :entityId")
    LiveData<Category> getById(int entityId);

    @Override
    @Query("select * from categories C where C.id = :entityId")
    Category getByIdBlocking(int entityId);

    @Override
    @Query("select * from categories C where C.title like '%'||:query||'%'")
    LiveData<List<Category>> search(String query);

    @Override
    @Query("delete from categories where id = :entityId")
    void delete(int entityId);

    @Override
    @Query("delete from categories")
    void deleteAll();
}
