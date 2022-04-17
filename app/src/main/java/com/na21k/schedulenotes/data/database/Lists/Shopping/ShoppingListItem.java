package com.na21k.schedulenotes.data.database.Lists.Shopping;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import com.na21k.schedulenotes.data.database.SimpleListItem;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "shopping_list_items", indices = {@Index(value = "id")})
public class ShoppingListItem extends SimpleListItem {

    @ColumnInfo(name = "price")
    private Float price;

    public ShoppingListItem(int id, @NotNull String text, Float price) {
        this.id = id;
        this.text = text;
        this.price = price;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
