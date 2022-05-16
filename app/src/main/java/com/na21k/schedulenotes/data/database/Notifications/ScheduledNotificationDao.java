package com.na21k.schedulenotes.data.database.Notifications;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ScheduledNotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ScheduledNotification notification);

    @Query("select * from scheduled_notifications N where N.request_id = :requestId")
    ScheduledNotification getByRequestIdBlocking(String requestId);

    @Query("delete from scheduled_notifications")
    void deleteAll();
}
