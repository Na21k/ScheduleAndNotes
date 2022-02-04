package com.na21k.schedulenotes.data.database.Lists.Music;

import androidx.room.Entity;
import androidx.room.Index;

import com.na21k.schedulenotes.data.database.SimpleListItem;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "music_list_items", indices = {@Index(value = "id")})
public class MusicListItem extends SimpleListItem {

    public MusicListItem(int id, @NotNull String text) {
        this.id = id;
        this.text = text;
    }
}
