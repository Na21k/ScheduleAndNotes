package com.na21k.schedulenotes.ui.controls;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.na21k.schedulenotes.databinding.DateTimeStartsEndsPickerBinding;
import com.na21k.schedulenotes.helpers.DateTimeHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DateTimeStartsEndsPickerView extends LinearLayout {

    private static final String DATE_TIME_STARTS_BUNDLE_KEY = "dateTimeStarts";
    private static final String DATE_TIME_ENDS_BUNDLE_KEY = "dateTimeEnds";
    private static final String SUPER_STATE_BUNDLE_KEY = "superState";
    private DateTimeStartsEndsPickerBinding mBinding;
    @NonNull
    private Date mSelectedDateTimeStarts = new Date();
    @NonNull
    private Date mSelectedDateTimeEnds = DateTimeHelper.addHours(mSelectedDateTimeStarts, 1);
    @Nullable
    private OnDateTimeSetListener mOnDateTimeStartsSetListener;
    @Nullable
    private OnDateTimeSetListener mOnDateTimeEndsSetListener;

    public DateTimeStartsEndsPickerView(Context context) {
        super(context);
        init();
    }

    public DateTimeStartsEndsPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DateTimeStartsEndsPickerView(Context context,
                                        @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DateTimeStartsEndsPickerView(Context context,
                                        AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater inflater = getContext().getSystemService(LayoutInflater.class);
        mBinding = DateTimeStartsEndsPickerBinding.inflate(inflater, this, true);

        mBinding.dateStarts.setOnClickListener(v -> createDateStartsPicker().show());
        mBinding.dateEnds.setOnClickListener(v -> createDateEndsPicker().show());
        mBinding.timeStarts.setOnClickListener(v -> createTimeStartsPicker().show());
        mBinding.timeEnds.setOnClickListener(v -> createTimeEndsPicker().show());
        mBinding.quickDateTimeSuggestionsPicker.setOnSelectedListener(
                this::setSelectedDateTimeStartsPreserveLongevity);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putLong(DATE_TIME_STARTS_BUNDLE_KEY, mSelectedDateTimeStarts.getTime());
        bundle.putLong(DATE_TIME_ENDS_BUNDLE_KEY, mSelectedDateTimeEnds.getTime());
        bundle.putParcelable(SUPER_STATE_BUNDLE_KEY, super.onSaveInstanceState());

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        long startsMillis = bundle.getLong(DATE_TIME_STARTS_BUNDLE_KEY);
        long endsMillis = bundle.getLong(DATE_TIME_ENDS_BUNDLE_KEY);
        mSelectedDateTimeStarts = new Date(startsMillis);
        mSelectedDateTimeEnds = new Date(endsMillis);
        updateDateTimeText();

        Parcelable superState = bundle.getParcelable(SUPER_STATE_BUNDLE_KEY);
        super.onRestoreInstanceState(superState);
    }

    @NonNull
    public Date getSelectedDateTimeStarts() {
        return mSelectedDateTimeStarts;
    }

    /**
     * @apiNote The value will be truncated to minutes
     */
    public void setSelectedDateTimeStarts(@NonNull Date selectedDateTimeStarts) {
        Objects.requireNonNull(selectedDateTimeStarts);
        mSelectedDateTimeStarts = DateTimeHelper.truncateSecondsAndMillis(selectedDateTimeStarts);
        updateDateTimeText();
    }

    /**
     * @apiNote The value will be truncated to minutes
     */
    public void setSelectedDateTimeStartsPreserveLongevity(@NonNull Date selectedDateTimeStarts) {
        Objects.requireNonNull(selectedDateTimeStarts);
        Date oldDateTime = mSelectedDateTimeStarts;
        Date newDateTime = DateTimeHelper.truncateSecondsAndMillis(selectedDateTimeStarts);

        Date diff = DateTimeHelper.getDifference(oldDateTime, newDateTime);
        adjustDateTimeEndsBy(diff);

        setSelectedDateTimeStarts(newDateTime);
    }

    @NonNull
    public Date getSelectedDateTimeEnds() {
        return mSelectedDateTimeEnds;
    }

    /**
     * @apiNote The value will be truncated to minutes
     */
    public void setSelectedDateTimeEnds(@NonNull Date selectedDateTimeEnds) {
        Objects.requireNonNull(selectedDateTimeEnds);
        mSelectedDateTimeEnds = DateTimeHelper.truncateSecondsAndMillis(selectedDateTimeEnds);
        updateDateTimeText();
    }

    public void setOnDateTimeStartsSetListener(OnDateTimeSetListener onDateTimeStartsSetListener) {
        mOnDateTimeStartsSetListener = onDateTimeStartsSetListener;
    }

    public void setOnDateTimeEndsSetListener(OnDateTimeSetListener onDateTimeEndsSetListener) {
        mOnDateTimeEndsSetListener = onDateTimeEndsSetListener;
    }

    private void updateDateTimeText() {
        String dateStarts = DateTimeHelper.getScheduleFormattedDate(mSelectedDateTimeStarts);
        String dateEnds = DateTimeHelper.getScheduleFormattedDate(mSelectedDateTimeEnds);
        String timeStarts = DateTimeHelper.getScheduleFormattedTime(mSelectedDateTimeStarts);
        String timeEnds = DateTimeHelper.getScheduleFormattedTime(mSelectedDateTimeEnds);

        mBinding.dateStarts.setText(dateStarts);
        mBinding.dateEnds.setText(dateEnds);
        mBinding.timeStarts.setText(timeStarts);
        mBinding.timeEnds.setText(timeEnds);
    }

    @NonNull
    private DatePickerDialog createDateStartsPicker() {
        return createDatePicker(mSelectedDateTimeStarts, (view, year, month, dayOfMonth) -> {
            Date oldDateTime = mSelectedDateTimeStarts;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(oldDateTime);
            calendar.set(year, month, dayOfMonth);
            Date newDateTime = calendar.getTime();

            Date diffDateOnly = DateTimeHelper.getDifference(oldDateTime, newDateTime);
            adjustDateTimeEndsBy(diffDateOnly);

            mSelectedDateTimeStarts = newDateTime;
            updateDateTimeText();

            if (mOnDateTimeStartsSetListener != null) {
                mOnDateTimeStartsSetListener.onDateTimeSet(mSelectedDateTimeStarts);
            }
        });
    }

    @NonNull
    private DatePickerDialog createDateEndsPicker() {
        return createDatePicker(mSelectedDateTimeEnds, (view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mSelectedDateTimeEnds);
            calendar.set(year, month, dayOfMonth);

            mSelectedDateTimeEnds = calendar.getTime();
            updateDateTimeText();

            if (mOnDateTimeEndsSetListener != null) {
                mOnDateTimeEndsSetListener.onDateTimeSet(mSelectedDateTimeEnds);
            }
        });
    }

    @NonNull
    private TimePickerDialog createTimeStartsPicker() {
        return createTimePicker(mSelectedDateTimeStarts, (view, hourOfDay, minute) -> {
            Date oldDateTime = mSelectedDateTimeStarts;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(oldDateTime);
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            Date newDateTime = calendar.getTime();

            Date diffTimeOnly = DateTimeHelper.getDifference(oldDateTime, newDateTime);
            adjustDateTimeEndsBy(diffTimeOnly);

            mSelectedDateTimeStarts = newDateTime;
            updateDateTimeText();

            if (mOnDateTimeStartsSetListener != null) {
                mOnDateTimeStartsSetListener.onDateTimeSet(mSelectedDateTimeStarts);
            }
        });
    }

    @NonNull
    private TimePickerDialog createTimeEndsPicker() {
        return createTimePicker(mSelectedDateTimeEnds, (view, hourOfDay, minute) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mSelectedDateTimeEnds);
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            mSelectedDateTimeEnds = calendar.getTime();
            updateDateTimeText();

            if (mOnDateTimeEndsSetListener != null) {
                mOnDateTimeEndsSetListener.onDateTimeSet(mSelectedDateTimeEnds);
            }
        });
    }

    @NonNull
    private DatePickerDialog createDatePicker(@NonNull Date date,
                                              DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getContext(), dateSetListener, year, month, day);
    }

    @NonNull
    private TimePickerDialog createTimePicker(@NonNull Date date,
                                              TimePickerDialog.OnTimeSetListener timeSetListener) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        boolean is24Hour = android.text.format.DateFormat.is24HourFormat(getContext());

        return new TimePickerDialog(getContext(), timeSetListener, hour, minute, is24Hour);
    }

    private void adjustDateTimeEndsBy(@NonNull Date spanLongevity) {
        mSelectedDateTimeEnds = DateTimeHelper.addDates(mSelectedDateTimeEnds, spanLongevity);
        updateDateTimeText();
    }

    public interface OnDateTimeSetListener {

        void onDateTimeSet(@NonNull Date dateTime);
    }
}
