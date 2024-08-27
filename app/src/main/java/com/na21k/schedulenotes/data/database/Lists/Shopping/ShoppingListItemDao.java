package com.na21k.schedulenotes.data.database.Lists.Shopping;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.na21k.schedulenotes.data.database.BaseDao;

import java.util.List;

@Dao
public interface ShoppingListItemDao extends BaseDao<ShoppingListItem> {

    @Query("select count(*) from shopping_list_items")
    int getCount();

    @Override
    @Query("select * from shopping_list_items")
    LiveData<List<ShoppingListItem>> getAll();

    @Override
    @Query("select * from shopping_list_items")
    List<ShoppingListItem> getAllBlocking();

    @Override
    @Query("select * from shopping_list_items I where I.id = :entityId")
    LiveData<ShoppingListItem> getById(int entityId);

    @Override
    @Query("select * from shopping_list_items I where I.id = :entityId")
    ShoppingListItem getByIdBlocking(int entityId);

    @Query("select * from shopping_list_items I where I.text like '%'||:search||'%'")
    LiveData<List<ShoppingListItem>> search(String search);

    @Override
    @Query("delete from shopping_list_items where id = :entityId")
    void delete(int entityId);

    @Query("delete from shopping_list_items")
    void deleteAll();

    @Query("delete from shopping_list_items where is_checked")
    void deleteChecked();
}
