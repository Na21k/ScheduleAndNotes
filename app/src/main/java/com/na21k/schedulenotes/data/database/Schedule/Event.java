package com.na21k.schedulenotes.data.database.Schedule;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Identifiable;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity(tableName = "events", indices = {@Index(value = "id"), @Index(value = "category_id")},
        foreignKeys = {@ForeignKey(entity = Category.class, parentColumns = "id",
                childColumns = "category_id", onDelete = ForeignKey.SET_NULL)})
public class Event extends Identifiable {

    @NotNull
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "details")
    private String details;

    @ColumnInfo(name = "category_id")
    private Integer categoryId;

    @NotNull
    @ColumnInfo(name = "date_time_starts")
    private Date dateTimeStarts;

    @NotNull
    @ColumnInfo(name = "date_time_ends")
    private Date dateTimeEnds;

    @ColumnInfo(name = "is_hidden", defaultValue = "false")
    private boolean isHidden;

    public Event(int id, @NotNull String title, String details, Integer categoryId,
                 @NotNull Date dateTimeStarts, @NotNull Date dateTimeEnds, boolean isHidden) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.categoryId = categoryId;
        this.dateTimeStarts = dateTimeStarts;
        this.dateTimeEnds = dateTimeEnds;
        this.isHidden = isHidden;
    }

    public Event(Event event) {
        this.title = event.title;
        this.details = event.details;
        this.categoryId = event.categoryId;
        this.dateTimeStarts = event.dateTimeStarts;
        this.dateTimeEnds = event.dateTimeEnds;
        this.isHidden = event.isHidden;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @NotNull
    public Date getDateTimeStarts() {
        return dateTimeStarts;
    }

    public void setDateTimeStarts(@NotNull Date dateTimeStarts) {
        this.dateTimeStarts = dateTimeStarts;
    }

    @NotNull
    public Date getDateTimeEnds() {
        return dateTimeEnds;
    }

    public void setDateTimeEnds(@NotNull Date dateTimeEnds) {
        this.dateTimeEnds = dateTimeEnds;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
