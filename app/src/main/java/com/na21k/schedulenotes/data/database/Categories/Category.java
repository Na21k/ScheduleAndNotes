package com.na21k.schedulenotes.data.database.Categories;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import com.na21k.schedulenotes.data.database.Identifiable;
import com.na21k.schedulenotes.data.models.ColorSet;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "categories",
        indices = {@Index(value = "id"), @Index(value = "title", unique = true)})
public class Category extends Identifiable {

    @NotNull
    @ColumnInfo(name = "title")
    private String title;

    /*@ColumnInfo(name = "color_hex")
    private int colorHex;*/

    @ColumnInfo(name = "color_set")
    private ColorSet colorSet;

    public Category(int id, @NotNull String title, /*int colorHex*/ColorSet colorSet) {
        this.id = id;
        this.title = title;
        //this.colorHex = colorHex;
        this.colorSet = colorSet;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    /*public int getColorHex() {
        return colorHex;
    }

    public void setColorHex(int colorHex) {
        this.colorHex = colorHex;
    }*/

    public ColorSet getColorSet() {
        return colorSet;
    }

    public void setColorSet(ColorSet colorSet) {
        this.colorSet = colorSet;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }
}
