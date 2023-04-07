package com.na21k.schedulenotes.data.database.Schedule;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.na21k.schedulenotes.data.database.Identifiable;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "event_notification_alarms_pending_intents",
        indices = {@Index(value = "id"), @Index(value = "event_id")},
        foreignKeys = {@ForeignKey(entity = Event.class,
                parentColumns = "id", childColumns = "event_id", onDelete = ForeignKey.CASCADE)})
public class EventNotificationAlarmPendingIntent extends Identifiable {

    public enum EventNotificationType {EventStarted, EventStartsSoon}

    @ColumnInfo(name = "event_id")
    private final int eventId;
    @NotNull
    @ColumnInfo(name = "notification_type")
    private final EventNotificationType notificationType;

    public EventNotificationAlarmPendingIntent(int id, int eventId,
                                               @NonNull EventNotificationType notificationType) {
        this.id = id;
        this.eventId = eventId;
        this.notificationType = notificationType;
    }

    public int getEventId() {
        return eventId;
    }

    @NonNull
    public EventNotificationType getNotificationType() {
        return notificationType;
    }
}
