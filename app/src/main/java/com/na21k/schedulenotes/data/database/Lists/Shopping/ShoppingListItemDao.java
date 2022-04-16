package com.na21k.schedulenotes.data.database.Lists.Shopping;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ShoppingListItemDao {

    @Query("select count(*) from shopping_list_items")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ShoppingListItem shoppingListItem);

    @Query("select * from shopping_list_items")
    LiveData<List<ShoppingListItem>> getAll();

    @Query("select * from shopping_list_items I where I.id = :id")
    LiveData<ShoppingListItem> getById(int id);

    @Update
    void update(ShoppingListItem shoppingListItem);

    @Delete
    void delete(ShoppingListItem shoppingListItem);
}
