package com.na21k.schedulenotes.data.database.Notes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Identifiable;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "notes", indices = {@Index(value = "id")},
        foreignKeys = {@ForeignKey(entity = Category.class, parentColumns = "id",
                childColumns = "category_id", onDelete = ForeignKey.SET_NULL)})
public class Note extends Identifiable {

    @NotNull
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "details")
    private String details;

    @ColumnInfo(name = "category_id")
    private Integer categoryId;

    public Note(int id, @NotNull String title, String details, Integer categoryId) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.categoryId = categoryId;
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
}
