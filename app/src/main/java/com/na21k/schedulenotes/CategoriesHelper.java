package com.na21k.schedulenotes;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.data.models.ColorSet;
import com.na21k.schedulenotes.data.models.ColorSetModel;
import com.na21k.schedulenotes.exceptions.CouldNotFindColorSetModelException;

import java.util.ArrayList;
import java.util.List;

public class CategoriesHelper {

    @NonNull
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

    public static int getNoteCategoryColor(Context context, Note note, List<Category> categories,
                                           boolean isNightMode) throws CouldNotFindColorSetModelException {
        Integer categoryId = note.getCategoryId();
        ColorSetModel defaultColorSetModel = getDefaultColorSetModel(context);
        int defaultColor = isNightMode ? defaultColorSetModel.getColorNightHex() :
                defaultColorSetModel.getColorDayHex();

        if (categoryId == null) {
            return defaultColor;
        }

        Category noteCategory = categories.stream().filter(
                category -> category.getId() == categoryId).findFirst().orElse(null);

        List<ColorSetModel> colorSetModels = getCategoriesColorSets(context);

        if (noteCategory == null) {
            return defaultColor;
        }

        ColorSetModel noteColorSetModel = colorSetModels.stream().filter(
                colorSetModel -> colorSetModel.getColorSet().equals(noteCategory.getColorSet()))
                .findFirst().orElse(null);

        if (noteColorSetModel == null) {
            return defaultColor;
        }

        return isNightMode ?
                noteColorSetModel.getColorNightHex() : noteColorSetModel.getColorDayHex();
    }

    public static int getEventCategoryColor(Context context, Event event, List<Category> categories,
                                            boolean isNightMode) throws CouldNotFindColorSetModelException {
        Integer categoryId = event.getCategoryId();
        ColorSetModel defaultColorSetModel = getDefaultColorSetModel(context);
        int defaultColor = isNightMode ? defaultColorSetModel.getColorNightHex() :
                defaultColorSetModel.getColorDayHex();

        if (categoryId == null) {
            return defaultColor;
        }

        Category eventCategory = categories.stream().filter(
                category -> category.getId() == categoryId).findFirst().orElse(null);

        List<ColorSetModel> colorSetModels = getCategoriesColorSets(context);

        if (eventCategory == null) {
            return defaultColor;
        }

        ColorSetModel eventColorSetModel = colorSetModels.stream().filter(
                colorSetModel -> colorSetModel.getColorSet().equals(eventCategory.getColorSet()))
                .findFirst().orElse(null);

        if (eventColorSetModel == null) {
            return defaultColor;
        }

        return isNightMode ?
                eventColorSetModel.getColorNightHex() : eventColorSetModel.getColorDayHex();
    }

    private static ColorSetModel getDefaultColorSetModel(Context context)
            throws CouldNotFindColorSetModelException {
        ColorSet defaultColorSet = Constants.DEFAULT_COLOR_SET;

        List<ColorSetModel> colorSetModels = CategoriesHelper.getCategoriesColorSets(context);
        ColorSetModel res = colorSetModels.stream().filter(
                colorSetModel -> colorSetModel.getColorSet().equals(defaultColorSet))
                .findFirst().orElse(null);

        if (res != null) {
            return res;
        } else {
            throw new CouldNotFindColorSetModelException(
                    "Could not find the default color set model", defaultColorSet);
        }
    }
}
