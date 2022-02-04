package com.na21k.schedulenotes.data.database.Schedule;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface EventDao {

    @Query("select count(*) from events")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Event event);

    @Query("select * from events")
    LiveData<List<Event>> getAll();

    @Query("select * from events E where E.title like '%'||:search||'%'" +
            "or E.details like '%'||:search||'%'" +
            "or E.category_id in" +
            "(select C.id from categories C where C.title like '%'||:search||'%')")
    LiveData<List<Event>> search(String search);

    @Query("delete from events where date_time_starts <= :date")
    void deleteOlderThan(Date date);
}
