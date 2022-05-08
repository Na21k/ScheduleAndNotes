package com.na21k.schedulenotes.data.database.Categories;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {

    @Query("select count(*) from categories")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);

    @Update
    void update(Category category);

    @Query("select * from categories")
    LiveData<List<Category>> getAll();

    @Query("select * from categories C where C.id = :id")
    LiveData<Category> getById(int id);

    @Query("select * from categories C where C.title like '%'||:search||'%'")
    LiveData<List<Category>> search(String search);

    @Delete
    void delete(Category category);

    @Query("delete from categories where id = :id")
    void delete(int id);
}
