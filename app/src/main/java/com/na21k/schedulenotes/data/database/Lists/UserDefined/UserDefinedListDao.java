package com.na21k.schedulenotes.data.database.Lists.UserDefined;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.CanClearDao;
import com.na21k.schedulenotes.data.database.CanSearchDao;

import java.util.List;

@Dao
public interface UserDefinedListDao extends BaseDao<UserDefinedList>,
        CanSearchDao<UserDefinedList>, CanClearDao {

    @Override
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(UserDefinedList entity);

    @Override
    @Query("select * from user_defined_lists")
    LiveData<List<UserDefinedList>> getAll();

    @Override
    @Query("select * from user_defined_lists")
    List<UserDefinedList> getAllBlocking();

    @Override
    @Query("select * from user_defined_lists L where L.id = :entityId")
    LiveData<UserDefinedList> getById(int entityId);

    @Override
    @Query("select * from user_defined_lists L where L.id = :entityId")
    UserDefinedList getByIdBlocking(int entityId);

    @Override
    @Query("select * from user_defined_lists E where E.title like '%'||:query||'%'")
    LiveData<List<UserDefinedList>> search(String query);

    @Override
    @Query("delete from user_defined_lists where id = :entityId")
    void delete(int entityId);

    @Override
    @Query("delete from user_defined_lists")
    void deleteAll();
}
