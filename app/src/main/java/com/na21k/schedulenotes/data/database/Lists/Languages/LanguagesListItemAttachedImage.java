package com.na21k.schedulenotes.data.database.Lists.Languages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.na21k.schedulenotes.data.database.Identifiable;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Entity(tableName = "languages_list_items_attached_images",
        indices = {@Index(value = "id"), @Index(value = "languages_list_item_id")},
        foreignKeys = {@ForeignKey(entity = LanguagesListItem.class, parentColumns = "id",
                childColumns = "languages_list_item_id", onDelete = ForeignKey.CASCADE)})
public class LanguagesListItemAttachedImage extends Identifiable {

    @ColumnInfo(name = "languages_list_item_id")
    private int languagesListItemId;

    @NotNull
    @ColumnInfo(name = "binary_data")
    private transient Bitmap bitmapData;

    @Ignore
    private transient Bitmap thumbnailBitmap;

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

    public Bitmap getThumbnailBitmap() {
        if (thumbnailBitmap == null) {
            int width = bitmapData.getWidth();
            int height = bitmapData.getHeight();
            final int maxTargetSide = 2000;

            if (width > maxTargetSide || height > maxTargetSide) {
                float requiredWidthFactor = 1f * width / maxTargetSide;
                float requiredHeightFactor = 1f * height / maxTargetSide;
                float requiredFactor = Math.max(requiredWidthFactor, requiredHeightFactor);

                width = (int) (width / requiredFactor);
                height = (int) (height / requiredFactor);
                thumbnailBitmap = ThumbnailUtils.extractThumbnail(bitmapData, width, height);
            } else {
                thumbnailBitmap = bitmapData;
            }
        }

        return thumbnailBitmap;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.defaultWriteObject();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapData.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();

        out.writeObject(bytes);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        byte[] bytes = (byte[]) in.readObject();
        bitmapData = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
