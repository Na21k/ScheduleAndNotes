package com.na21k.schedulenotes.data.database.Categories;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.na21k.schedulenotes.data.database.BaseDao;

import java.util.List;

@Dao
public interface CategoryDao extends BaseDao<Category> {

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

    @Query("select * from categories C where C.title like '%'||:search||'%'")
    LiveData<List<Category>> search(String search);

    @Override
    @Query("delete from categories where id = :entityId")
    void delete(int entityId);

    @Query("delete from categories")
    void deleteAll();
}
