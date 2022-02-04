package com.na21k.schedulenotes.data.database;

import androidx.room.ColumnInfo;

import org.jetbrains.annotations.NotNull;

public abstract class SimpleListItem extends Identifiable {

    @NotNull
    @ColumnInfo(name = "text")
    protected String text;

    @NotNull
    public String getText() {
        return text;
    }

    public void setText(@NotNull String text) {
        this.text = text;
    }
}
