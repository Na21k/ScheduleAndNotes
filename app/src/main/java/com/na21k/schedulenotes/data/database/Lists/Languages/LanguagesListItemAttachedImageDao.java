package com.na21k.schedulenotes.data.database.Lists.Languages;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.na21k.schedulenotes.data.database.BaseDao;

import java.util.List;

@Dao
public interface LanguagesListItemAttachedImageDao extends BaseDao<LanguagesListItemAttachedImage> {

    @Override
    @Query("select * from languages_list_items_attached_images")
    LiveData<List<LanguagesListItemAttachedImage>> getAll();

    @Override
    @Query("select * from languages_list_items_attached_images")
    List<LanguagesListItemAttachedImage> getAllBlocking();

    @Override
    @Query("select * from  languages_list_items_attached_images where id = :entityId")
    LiveData<LanguagesListItemAttachedImage> getById(int entityId);

    @Override
    @Query("select * from  languages_list_items_attached_images where id = :entityId")
    LanguagesListItemAttachedImage getByIdBlocking(int entityId);

    @Query("select * from languages_list_items_attached_images I " +
            "where I.languages_list_item_id = :listItemId")
    LiveData<List<LanguagesListItemAttachedImage>> getByListItemId(int listItemId);

    @Query("select * from languages_list_items_attached_images I " +
            "where I.languages_list_item_id = :listItemId")
    List<LanguagesListItemAttachedImage> getByListItemIdBlocking(int listItemId);

    @Query("select languages_list_item_id from languages_list_items_attached_images")
    LiveData<List<Integer>> getAllListItemIds();

    @Override
    @Query("delete from languages_list_items_attached_images where id = :entityId")
    void delete(int entityId);
}
