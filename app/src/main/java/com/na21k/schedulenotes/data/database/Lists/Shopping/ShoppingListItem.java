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
    @ColumnInfo(name = "count")
    private Integer count;

    public ShoppingListItem(int id, @NotNull String text, Float price, Integer count) {
        this.id = id;
        this.text = text;
        this.price = price;
        this.count = count;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
