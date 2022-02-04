package com.na21k.schedulenotes.data.database.Lists.UserDefined;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import com.na21k.schedulenotes.data.database.Identifiable;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "user_defined_lists",
        indices = {@Index(value = "id"), @Index(value = "title", unique = true)})
public class UserDefinedList extends Identifiable {

    @NotNull
    @ColumnInfo(name = "title")
    private String title;

    public UserDefinedList(int id, @NotNull String title) {
        this.id = id;
        this.title = title;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }
}
