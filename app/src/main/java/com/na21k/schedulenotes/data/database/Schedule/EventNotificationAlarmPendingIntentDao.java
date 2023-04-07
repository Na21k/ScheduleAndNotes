package com.na21k.schedulenotes.data.database.Schedule;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EventNotificationAlarmPendingIntentDao {

    @Insert
    long insert(EventNotificationAlarmPendingIntent pendingIntent);

    @Query("select * from event_notification_alarms_pending_intents")
    List<EventNotificationAlarmPendingIntent> getAllBlocking();

    @Query("select * from event_notification_alarms_pending_intents I where I.id = :id")
    EventNotificationAlarmPendingIntent getByIdBlocking(int id);

    @Query("select * from event_notification_alarms_pending_intents I where I.event_id = :eventId")
    List<EventNotificationAlarmPendingIntent> getByEventIdBlocking(int eventId);

    @Delete
    void delete(EventNotificationAlarmPendingIntent pendingIntent);

    @Query("delete from event_notification_alarms_pending_intents")
    void deleteAll();
}
