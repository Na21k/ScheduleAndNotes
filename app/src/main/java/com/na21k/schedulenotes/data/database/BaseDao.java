package com.na21k.schedulenotes.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

public interface BaseDao<TEntity extends Identifiable> {

    LiveData<List<TEntity>> getAll();

    List<TEntity> getAllBlocking();

    LiveData<TEntity> getById(int entityId);

    TEntity getByIdBlocking(int entityId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<TEntity> entities);

    @Update
    void update(TEntity entity);

    @Delete
    void delete(TEntity entity);

    void delete(int entityId);
}
