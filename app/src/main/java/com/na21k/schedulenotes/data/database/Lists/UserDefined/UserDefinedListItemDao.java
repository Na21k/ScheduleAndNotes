package com.na21k.schedulenotes.data.database.Lists.UserDefined;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDefinedListItemDao {

    @Query("select count(*) from user_defined_lists_items")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserDefinedListItem userDefinedListItem);

    @Query("select * from user_defined_lists_items")
    LiveData<List<UserDefinedListItem>> getAll();

    @Query("select * from user_defined_lists_items I where I.id = :id")
    LiveData<UserDefinedListItem> getById(int id);

    @Query("select * from user_defined_lists_items I where I.list_id = :listId")
    LiveData<List<UserDefinedListItem>> getByListId(int listId);
}
