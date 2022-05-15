package com.na21k.schedulenotes.data.database.Notifications;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import com.na21k.schedulenotes.data.database.Identifiable;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "scheduled_notifications",
        indices = {@Index(value = "id"), @Index(value = "request_id", unique = true)})
public class ScheduledNotification extends Identifiable {

    @NotNull
    @ColumnInfo(name = "request_id")
    private final String requestId;

    public ScheduledNotification(int id, @NotNull String requestId) {
        this.id = id;
        this.requestId = requestId;
    }

    @NotNull
    public String getRequestId() {
        return requestId;
    }
}
