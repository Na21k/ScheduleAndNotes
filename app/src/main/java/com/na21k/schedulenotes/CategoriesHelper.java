package com.na21k.schedulenotes;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.na21k.schedulenotes.data.models.ColorSet;
import com.na21k.schedulenotes.data.models.ColorSetModel;

import java.util.ArrayList;
import java.util.List;

public class CategoriesHelper {

    public static List<ColorSetModel> getCategoriesColorSets(Context context) {
        int colorGrayDay = ContextCompat.getColor(context, R.color.category_gray);
        int colorGrayNight = ContextCompat.getColor(context, R.color.category_gray_night);
        int colorRedDay = ContextCompat.getColor(context, R.color.category_red);
        int colorRedNight = ContextCompat.getColor(context, R.color.category_red_night);
        int colorOrangeDay = ContextCompat.getColor(context, R.color.category_orange);
        int colorOrangeNight = ContextCompat.getColor(context, R.color.category_orange_night);
        int colorYellowDay = ContextCompat.getColor(context, R.color.category_yellow);
        int colorYellowNight = ContextCompat.getColor(context, R.color.category_yellow_night);
        int colorGreenDay = ContextCompat.getColor(context, R.color.category_green);
        int colorGreenNight = ContextCompat.getColor(context, R.color.category_green_night);
        int colorCyanDay = ContextCompat.getColor(context, R.color.category_cyan);
        int colorCyanNight = ContextCompat.getColor(context, R.color.category_cyan_night);
        int colorBlueDay = ContextCompat.getColor(context, R.color.category_blue);
        int colorBlueNight = ContextCompat.getColor(context, R.color.category_blue_night);
        int colorPurpleDay = ContextCompat.getColor(context, R.color.category_purple);
        int colorPurpleNight = ContextCompat.getColor(context, R.color.category_purple_night);

        List<ColorSetModel> res = new ArrayList<>();
        res.add(new ColorSetModel(ColorSet.GRAY, colorGrayDay, colorGrayNight));
        res.add(new ColorSetModel(ColorSet.RED, colorRedDay, colorRedNight));
        res.add(new ColorSetModel(ColorSet.ORANGE, colorOrangeDay, colorOrangeNight));
        res.add(new ColorSetModel(ColorSet.YELLOW, colorYellowDay, colorYellowNight));
        res.add(new ColorSetModel(ColorSet.GREEN, colorGreenDay, colorGreenNight));
        res.add(new ColorSetModel(ColorSet.CYAN, colorCyanDay, colorCyanNight));
        res.add(new ColorSetModel(ColorSet.BLUE, colorBlueDay, colorBlueNight));
        res.add(new ColorSetModel(ColorSet.PURPLE, colorPurpleDay, colorPurpleNight));

        return res;
    }
}
