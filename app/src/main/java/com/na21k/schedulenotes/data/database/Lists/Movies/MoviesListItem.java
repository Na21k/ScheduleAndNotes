package com.na21k.schedulenotes.data.database.Lists.Movies;

import androidx.room.Entity;
import androidx.room.Index;

import com.na21k.schedulenotes.data.database.SimpleListItem;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "movies_list_items", indices = {@Index(value = "id")})
public class MoviesListItem extends SimpleListItem {

    public MoviesListItem(int id, @NotNull String text) {
        this.id = id;
        this.text = text;
    }
}
