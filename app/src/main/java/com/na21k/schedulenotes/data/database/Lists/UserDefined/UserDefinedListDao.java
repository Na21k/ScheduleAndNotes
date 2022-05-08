package com.na21k.schedulenotes.data.database.Lists.UserDefined;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDefinedListDao {

    @Query("select count(*) from user_defined_lists")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserDefinedList userDefinedList);

    @Query("select * from user_defined_lists")
    LiveData<List<UserDefinedList>> getAll();

    @Query("select * from user_defined_lists E where E.title like '%'||:search||'%'")
    LiveData<List<UserDefinedList>> search(String search);

    @Query("select * from user_defined_lists L where L.id = :id")
    LiveData<UserDefinedList> getById(int id);

    @Update
    void update(UserDefinedList list);

    @Delete
    void delete(UserDefinedList list);
}
