package com.na21k.schedulenotes.data.database.Lists.Languages;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.na21k.schedulenotes.data.database.Identifiable;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "languages_list_items_attached_images",
        indices = {@Index(value = "id"), @Index(value = "languages_list_item_id")},
        foreignKeys = {@ForeignKey(entity = LanguagesListItem.class, parentColumns = "id",
                childColumns = "languages_list_item_id", onDelete = ForeignKey.CASCADE)})
public class LanguagesListItemAttachedImage extends Identifiable {

    @ColumnInfo(name = "languages_list_item_id")
    private int languagesListItemId;

    @NotNull
    @ColumnInfo(name = "binary_data")
    private Bitmap bitmapData;

    @Ignore
    private Bitmap thumbnailBitmap;

    @Ignore
    public LanguagesListItemAttachedImage(int id, @NotNull Bitmap bitmapData) {
        this.id = id;
        this.bitmapData = bitmapData;
    }

    public LanguagesListItemAttachedImage(int id, int languagesListItemId,
                                          @NotNull Bitmap bitmapData) {
        this.id = id;
        this.languagesListItemId = languagesListItemId;
        this.bitmapData = bitmapData;
    }

    public int getLanguagesListItemId() {
        return languagesListItemId;
    }

    public void setLanguagesListItemId(int languagesListItemId) {
        this.languagesListItemId = languagesListItemId;
    }

    @NotNull
    public Bitmap getBitmapData() {
        return bitmapData;
    }

    public void setBitmapData(@NotNull Bitmap bitmapData) {
        this.bitmapData = bitmapData;
    }

    public Bitmap getThumbnailBitmap() {
        if (thumbnailBitmap == null) {
            int width = bitmapData.getWidth();
            int height = bitmapData.getHeight();
            final int maxTargetWidth = 2000;

            if (width > maxTargetWidth) {
                width = (int) (width * 0.3f);
                height = (int) (height * 0.3f);
                thumbnailBitmap = ThumbnailUtils.extractThumbnail(bitmapData, width, height);
            } else {
                thumbnailBitmap = bitmapData;
            }
        }

        return thumbnailBitmap;
    }
}
