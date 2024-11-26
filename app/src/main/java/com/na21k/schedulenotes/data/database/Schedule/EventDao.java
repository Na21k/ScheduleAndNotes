package com.na21k.schedulenotes.data.database.Schedule;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.CanClearDao;
import com.na21k.schedulenotes.data.database.CanSearchDao;

import java.util.Date;
import java.util.List;

@Dao
public interface EventDao extends BaseDao<Event>, CanSearchDao<Event>, CanClearDao {

    @Override
    @Query("select * from events")
    LiveData<List<Event>> getAll();

    @Override
    @Query("select * from events")
    List<Event> getAllBlocking();

    @Override
    @Query("select * from events E where E.id = :entityId")
    LiveData<Event> getById(int entityId);

    @Override
    @Query("select * from events E where E.id = :entityId")
    Event getByIdBlocking(int entityId);

    @Query("select * from events E where E.date_time_starts < :hasStartedBefore " +
            "and E.date_time_ends >= :hasNotEndedBy")
    LiveData<List<Event>> getByDate(Date hasStartedBefore, Date hasNotEndedBy);

    @Override
    @Query("select * from events E where E.title like '%'||:query||'%'" +
            "or E.details like '%'||:query||'%'" +
            "or E.category_id in" +
            "(select C.id from categories C where C.title like '%'||:query||'%')")
    LiveData<List<Event>> search(String query);

    @Override
    @Query("delete from events where id = :entityId")
    void delete(int entityId);

    @Query("delete from events where date_time_ends <= :date")
    void deleteOlderThan(Date date);

    @Override
    @Query("delete from events")
    void deleteAll();
}
