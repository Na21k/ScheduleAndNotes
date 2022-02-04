package com.na21k.schedulenotes.data.models;

public class ColorSetModel {

    private final ColorSet colorSet;
    private final int colorDayHex;
    private final int colorNightHex;

    public ColorSetModel(ColorSet colorSet, int colorDayHex, int colorNightHex) {
        this.colorSet = colorSet;
        this.colorDayHex = colorDayHex;
        this.colorNightHex = colorNightHex;
    }

    public ColorSet getColorSet() {
        return colorSet;
    }

    public int getColorDayHex() {
        return colorDayHex;
    }

    public int getColorNightHex() {
        return colorNightHex;
    }
}
