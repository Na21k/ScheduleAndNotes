package com.na21k.schedulenotes.helpers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Categories.Category;
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
        int colorPinkDay = ContextCompat.getColor(context, R.color.category_pink);
        int colorPinkNight = ContextCompat.getColor(context, R.color.category_pink_night);

        List<ColorSetModel> res = new ArrayList<>();
        res.add(new ColorSetModel(ColorSet.GRAY, colorGrayDay, colorGrayNight));
        res.add(new ColorSetModel(ColorSet.RED, colorRedDay, colorRedNight));
        res.add(new ColorSetModel(ColorSet.ORANGE, colorOrangeDay, colorOrangeNight));
        res.add(new ColorSetModel(ColorSet.YELLOW, colorYellowDay, colorYellowNight));
        res.add(new ColorSetModel(ColorSet.GREEN, colorGreenDay, colorGreenNight));
        res.add(new ColorSetModel(ColorSet.CYAN, colorCyanDay, colorCyanNight));
        res.add(new ColorSetModel(ColorSet.BLUE, colorBlueDay, colorBlueNight));
        res.add(new ColorSetModel(ColorSet.PURPLE, colorPurpleDay, colorPurpleNight));
        res.add(new ColorSetModel(ColorSet.PINK, colorPinkDay, colorPinkNight));

        return res;
    }

    public static int getCategoryColor(Context context, Integer categoryId,
                                       List<Category> categories, boolean isNightMode)
            throws CouldNotFindColorSetModelException {
        final int defaultColor = getDefaultCategoryColor(context, isNightMode);

        if (categoryId == null) {
            return defaultColor;
        }

        Category category = categories.stream().filter(c -> c.getId() == categoryId).findFirst()
                .orElse(null);

        return getCategoryColor(context, category, isNightMode, defaultColor);
    }

    public static int getCategoryColor(Context context, Category category, boolean isNightMode)
            throws CouldNotFindColorSetModelException {
        final int defaultColor = getDefaultCategoryColor(context, isNightMode);
        return getCategoryColor(context, category, isNightMode, defaultColor);
    }

    private static int getCategoryColor(Context context, Category category, boolean isNightMode,
                                        int defaultColor) {
        if (category == null) {
            return defaultColor;
        }

        List<ColorSetModel> colorSetModels = getCategoriesColorSets(context);
        ColorSetModel colorSetModel = colorSetModels.stream().filter(
                        csm -> csm.getColorSet() == category.getColorSet()).findFirst()
                .orElse(null);

        if (colorSetModel == null) {
            return defaultColor;
        }

        return isNightMode ? colorSetModel.getColorNightHex() : colorSetModel.getColorDayHex();
    }

    private static int getDefaultCategoryColor(Context context, boolean isNightMode)
            throws CouldNotFindColorSetModelException {
        ColorSetModel defaultColorSetModel = getDefaultColorSetModel(context);
        return isNightMode ?
                defaultColorSetModel.getColorNightHex() : defaultColorSetModel.getColorDayHex();
    }

    @NonNull
    private static ColorSetModel getDefaultColorSetModel(Context context)
            throws CouldNotFindColorSetModelException {
        return getDefaultColorSetModel(getCategoriesColorSets(context));
    }

    @NonNull
    public static ColorSetModel getDefaultColorSetModel(@NonNull List<ColorSetModel> models)
            throws CouldNotFindColorSetModelException {
        ColorSet defaultColorSet = Constants.DEFAULT_COLOR_SET;
        return getColorSetModelByColorSet(models, defaultColorSet);
    }

    @NonNull
    public static ColorSetModel getColorSetModelByColorSet(@NonNull List<ColorSetModel> models,
                                                           @NonNull ColorSet colorSet)
            throws CouldNotFindColorSetModelException {
        ColorSetModel res = models.stream().filter(colorSetModel ->
                        colorSetModel.getColorSet() == colorSet)
                .findFirst().orElse(null);

        if (res != null) {
            return res;
        } else {
            throw new CouldNotFindColorSetModelException(
                    "Could not find the default color set model", colorSet);
        }
    }
}
