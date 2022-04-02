package com.na21k.schedulenotes.data.database.Lists.UserDefined;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.na21k.schedulenotes.data.database.SimpleListItem;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "user_defined_lists_items",
        indices = {@Index(value = "id"), @Index(value = "list_id")},
        foreignKeys = {@ForeignKey(entity = UserDefinedList.class,
                parentColumns = "id", childColumns = "list_id", onDelete = ForeignKey.CASCADE)})
public class UserDefinedListItem extends SimpleListItem {

    @ColumnInfo(name = "list_id")
    private int listId;

    public UserDefinedListItem(int id, @NotNull String text, int listId) {
        this.id = id;
        this.text = text;
        this.listId = listId;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }
}
