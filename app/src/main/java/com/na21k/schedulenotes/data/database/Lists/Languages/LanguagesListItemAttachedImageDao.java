package com.na21k.schedulenotes.data.database.Lists.Languages;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LanguagesListItemAttachedImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LanguagesListItemAttachedImage attachedImage);

    @Query("select * from languages_list_items_attached_images I " +
            "where I.languages_list_item_id = :listItemId")
    LiveData<List<LanguagesListItemAttachedImage>> getByListItemId(int listItemId);

    @Query("select languages_list_item_id from languages_list_items_attached_images")
    LiveData<List<Integer>> getAllListItemIds();

    @Update
    void update(LanguagesListItemAttachedImage attachedImage);

    @Delete
    void delete(LanguagesListItemAttachedImage attachedImage);
}
