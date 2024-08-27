package com.na21k.schedulenotes.data.database.Lists.UserDefined;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.na21k.schedulenotes.data.database.BaseDao;

import java.util.List;

@Dao
public interface UserDefinedListItemDao extends BaseDao<UserDefinedListItem> {

    @Override
    @Query("select * from user_defined_lists_items")
    LiveData<List<UserDefinedListItem>> getAll();

    @Override
    @Query("select * from user_defined_lists_items")
    List<UserDefinedListItem> getAllBlocking();

    @Override
    @Query("select * from user_defined_lists_items I where I.id = :entityId")
    LiveData<UserDefinedListItem> getById(int entityId);

    @Override
    @Query("select * from user_defined_lists_items I where I.id = :entityId")
    UserDefinedListItem getByIdBlocking(int entityId);

    @Query("select * from user_defined_lists_items I where I.list_id = :listId")
    LiveData<List<UserDefinedListItem>> getByListId(int listId);

    @Query("select * from user_defined_lists_items I where I.list_id = :listId " +
            "and I.text like '%'||:search||'%'")
    LiveData<List<UserDefinedListItem>> searchInList(int listId, String search);

    @Override
    @Query("delete from user_defined_lists_items where id = :entityId")
    void delete(int entityId);
}
