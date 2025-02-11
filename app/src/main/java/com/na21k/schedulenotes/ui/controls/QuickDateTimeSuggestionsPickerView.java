package com.na21k.schedulenotes.ui.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.helpers.DateTimeHelper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class QuickDateTimeSuggestionsPickerView extends ChipGroup {

    private enum SuggestionType {
        TodayAfternoon, TodayEvening, TomorrowNoon, TomorrowAfternoon, TomorrowEvening
    }

    private OnDateTimeSelectedListener mOnSelectedListener;

    public QuickDateTimeSuggestionsPickerView(Context context) {
        super(context);
        addSuggestions();
    }

    public QuickDateTimeSuggestionsPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addSuggestions();
    }

    public QuickDateTimeSuggestionsPickerView(Context context, AttributeSet attrs,
                                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addSuggestions();
    }

    private void addSuggestions() {
        List<Chip> suggestions = Arrays.stream(SuggestionType.values()).map(type -> {
            int chipTextRes;

            switch (type) {
                case TodayAfternoon:
                    chipTextRes = R.string.today_afternoon;
                    break;
                case TodayEvening:
                    chipTextRes = R.string.today_evening;
                    break;
                case TomorrowNoon:
                    chipTextRes = R.string.tomorrow_noon;
                    break;
                case TomorrowAfternoon:
                    chipTextRes = R.string.tomorrow_afternoon;
                    break;
                case TomorrowEvening:
                    chipTextRes = R.string.tomorrow_evening;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }

            return createChip(getStringRes(chipTextRes), type);
        }).collect(Collectors.toList());

        suggestions.forEach(this::addView);
    }

    @NonNull
    private Chip createChip(String text, SuggestionType type) {
        Chip chip = new Chip(getContext());

        ShapeAppearanceModel shapeAppearanceModel = chip.getShapeAppearanceModel()
                .withCornerSize(getResources().getDimension(R.dimen.corner_radius));
        chip.setShapeAppearanceModel(shapeAppearanceModel);

        chip.setText(text);
        chip.setTag(type);
        chip.setOnClickListener(this::onChipClick);

        return chip;
    }

    private void onChipClick(@NonNull View chip) {
        SuggestionType type = (SuggestionType) chip.getTag();
        Date today = DateTimeHelper.truncateToDateOnly(new Date());
        Date tomorrow = DateTimeHelper.addDays(today, 1);

        Date resDateTime;

        switch (type) {
            case TodayAfternoon:
                resDateTime = DateTimeHelper.addHours(today, 13);
                break;
            case TodayEvening:
                resDateTime = DateTimeHelper.addHours(today, 20);
                break;
            case TomorrowNoon:
                resDateTime = DateTimeHelper.addHours(tomorrow, 12);
                break;
            case TomorrowAfternoon:
                resDateTime = DateTimeHelper.addHours(tomorrow, 13);
                break;
            case TomorrowEvening:
                resDateTime = DateTimeHelper.addHours(tomorrow, 20);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        if (mOnSelectedListener != null) {
            mOnSelectedListener.onSelected(resDateTime);
        }
    }

    @NonNull
    private String getStringRes(@StringRes int id) {
        return getResources().getString(id);
    }

    public void setOnSelectedListener(OnDateTimeSelectedListener onSelected) {
        mOnSelectedListener = onSelected;
    }

    public interface OnDateTimeSelectedListener {

        void onSelected(@NonNull Date dateTime);
    }
}
