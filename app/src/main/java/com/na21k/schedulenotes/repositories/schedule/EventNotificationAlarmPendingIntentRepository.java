package com.na21k.schedulenotes.repositories.schedule;

import com.na21k.schedulenotes.data.database.Schedule.EventNotificationAlarmPendingIntent;

import java.util.List;

public interface EventNotificationAlarmPendingIntentRepository {

    List<EventNotificationAlarmPendingIntent> getByEventIdBlocking(int eventId);
}
