package com.na21k.schedulenotes.data.database.Schedule;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.na21k.schedulenotes.data.database.BaseDao;

import java.util.List;

@Dao
public interface EventNotificationAlarmPendingIntentDao
        extends BaseDao<EventNotificationAlarmPendingIntent> {

    @Override
    @Query("select * from event_notification_alarms_pending_intents")
    LiveData<List<EventNotificationAlarmPendingIntent>> getAll();

    @Override
    @Query("select * from event_notification_alarms_pending_intents")
    List<EventNotificationAlarmPendingIntent> getAllBlocking();

    @Override
    @Query("select * from event_notification_alarms_pending_intents I where I.id = :entityId")
    LiveData<EventNotificationAlarmPendingIntent> getById(int entityId);

    @Override
    @Query("select * from event_notification_alarms_pending_intents I where I.id = :entityId")
    EventNotificationAlarmPendingIntent getByIdBlocking(int entityId);

    @Query("select * from event_notification_alarms_pending_intents I where I.event_id = :eventId")
    List<EventNotificationAlarmPendingIntent> getByEventIdBlocking(int eventId);

    @Override
    @Query("delete from event_notification_alarms_pending_intents where id = :entityId")
    void delete(int entityId);

    @Query("delete from event_notification_alarms_pending_intents")
    void deleteAll();
}
