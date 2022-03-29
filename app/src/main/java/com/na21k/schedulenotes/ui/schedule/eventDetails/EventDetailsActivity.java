package com.na21k.schedulenotes.ui.schedule.eventDetails;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.databinding.ActivityEventDetailsBinding;
import com.na21k.schedulenotes.helpers.DateTimeHelper;
import com.na21k.schedulenotes.ui.categories.categoryDetails.CategoryDetailsActivity;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EventDetailsActivity extends AppCompatActivity implements Observer<Event> {

    private EventDetailsViewModel mViewModel;
    private ActivityEventDetailsBinding mBinding;
    private Integer mCurrentEventsCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);
        mBinding = ActivityEventDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setPickersListeners();
        Bundle bundle = getIntent().getExtras();

        if (isEditing()) {
            setTitle(R.string.title_edit_event);

            int eventId = bundle.getInt(Constants.EVENT_ID_INTENT_KEY);
            loadEventFromDb(eventId);
        } else {
            setTitle(R.string.title_create_event);

            long millis = bundle.getLong(Constants.SELECTED_TIME_MILLIS_INTENT_KEY);
            setSelectedDateTimes(millis);
        }

        loadCategoriesFromDb();
    }

    @Override
    public void onChanged(Event event) {
        mBinding.eventTitle.setText(event.getTitle());
        mBinding.eventDetails.setText(event.getDetails());
        mCurrentEventsCategoryId = event.getCategoryId();

        Date starts = event.getDateTimeStarts();
        Date ends = event.getDateTimeEnds();

        mViewModel.setSelectedDateTimeStarts(starts);
        mViewModel.setSelectedDateTimeEnds(ends);

        mBinding.dateStarts.setText(DateTimeHelper.getScheduleFormattedDate(starts));
        mBinding.timeStarts.setText(DateTimeHelper.getScheduleFormattedTime(starts));
        mBinding.dateEnds.setText(DateTimeHelper.getScheduleFormattedDate(ends));
        mBinding.timeEnds.setText(DateTimeHelper.getScheduleFormattedTime(ends));

        mBinding.isHidden.setChecked(event.isHidden());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_details_menu, menu);

        if (!isEditing()) {
            menu.removeItem(R.id.menu_delete);
        }

        if (mCurrentEventsCategoryId == null) {
            menu.removeItem(R.id.menu_remove_category);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                saveEvent();
                break;
            case R.id.menu_cancel:
                finish();
                break;
            case R.id.menu_delete:
                deleteEvent();
                break;
            case R.id.menu_set_category:
                showCategorySelection();
                break;
            case R.id.menu_remove_category:
                removeCategory();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void saveEvent() {
        Editable titleEditable = mBinding.eventTitle.getText();
        Editable detailsEditable = mBinding.eventDetails.getText();
        boolean isHidden = mBinding.isHidden.isChecked();
        Date starts = DateTimeHelper.truncateSecondsAndMillis(mViewModel.getSelectedDateTimeStarts());
        Date ends = DateTimeHelper.truncateSecondsAndMillis(mViewModel.getSelectedDateTimeEnds());

        if (titleEditable == null || titleEditable.toString().isEmpty()) {
            showSnackbar(R.string.specify_event_title_snackbar);
            return;
        }

        if (Objects.requireNonNull(starts).compareTo(ends) > 0) {
            showSnackbar(R.string.start_time_less_than_end_time_snackbar);
            return;
        }

        Event event = new Event(0, titleEditable.toString(), detailsEditable.toString(),
                mCurrentEventsCategoryId, starts, Objects.requireNonNull(ends), isHidden);

        if (isEditing()) {
            mViewModel.updateCurrentEvent(event);
        } else {
            mViewModel.createEvent(event);
        }

        finish();
    }

    private void deleteEvent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_24);
        builder.setTitle(R.string.event_deletion_alert_title);
        builder.setMessage(R.string.event_deletion_alert_message);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            LiveData<Event> event = mViewModel.getCurrentEvent();

            if (event != null) {
                event.removeObserver(this);
                mViewModel.deleteCurrentEvent();
                finish();
            }
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }

    private void removeCategory() {
        mCurrentEventsCategoryId = null;
        showSnackbar(R.string.excluded_from_category_snackbar);

        invalidateOptionsMenu();    //hide the Exclude from category button
    }

    private void showCategorySelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_categories_24);
        builder.setTitle(R.string.pick_category_dialog_title);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.create_category_dialog_button, (dialog, which) -> {
            Intent intent = new Intent(this, CategoryDetailsActivity.class);
            startActivity(intent);
        });

        List<Category> categories = mViewModel.getCategoriesCache();

        if (categories != null) {
            categories.sort(Comparator.comparing(Category::getTitle));

            ArrayAdapter<Category> adapter = new ArrayAdapter<>(EventDetailsActivity.this,
                    android.R.layout.simple_list_item_1, categories);
            builder.setAdapter(adapter, (dialog, which) -> {
                mCurrentEventsCategoryId = categories.get(which).getId();
                showSnackbar(R.string.category_set_snackbar);
                invalidateOptionsMenu();    //show the Exclude from category button
            });

            builder.show();
        }
    }

    private void setPickersListeners() {
        mBinding.dateStarts.setOnClickListener(v -> createDateStartsPicker().show());
        mBinding.dateEnds.setOnClickListener(v -> createDateEndsPicker().show());
        mBinding.timeStarts.setOnClickListener(v -> createTimeStartsPicker().show());
        mBinding.timeEnds.setOnClickListener(v -> createTimeEndsPicker().show());
    }

    private DatePickerDialog createDateStartsPicker() {
        Date date = mViewModel.getSelectedDateTimeStarts();
        return createDatePicker(date, this::onDateStartsSet);
    }

    private DatePickerDialog createDateEndsPicker() {
        Date date = mViewModel.getSelectedDateTimeEnds();
        return createDatePicker(date, this::onDateEndsSet);
    }

    private TimePickerDialog createTimeStartsPicker() {
        Date date = mViewModel.getSelectedDateTimeStarts();
        return createTimePicker(date, this::onTimeStartsSet);
    }

    private TimePickerDialog createTimeEndsPicker() {
        Date date = mViewModel.getSelectedDateTimeEnds();
        return createTimePicker(date, this::onTimeEndsSet);
    }

    private DatePickerDialog createDatePicker(Date date,
                                              DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(this, dateSetListener, year, month, day);
    }

    private TimePickerDialog createTimePicker(Date date,
                                              TimePickerDialog.OnTimeSetListener timeSetListener) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        boolean is24Hour = android.text.format.DateFormat.is24HourFormat(this);

        return new TimePickerDialog(this, timeSetListener, hour, minute, is24Hour);
    }

    private void onDateStartsSet(DatePicker view, int year, int month, int dayOfMonth) {
        Date oldDate = mViewModel.getSelectedDateTimeStarts();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Objects.requireNonNull(oldDate));
        calendar.set(year, month, dayOfMonth);
        Date newDate = calendar.getTime();

        mViewModel.setSelectedDateTimeStarts(newDate);
        mBinding.dateStarts.setText(DateTimeHelper.getScheduleFormattedDate(newDate));
    }

    private void onDateEndsSet(DatePicker view, int year, int month, int dayOfMonth) {
        Date oldDate = mViewModel.getSelectedDateTimeEnds();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Objects.requireNonNull(oldDate));
        calendar.set(year, month, dayOfMonth);
        Date newDate = calendar.getTime();

        mViewModel.setSelectedDateTimeEnds(newDate);
        mBinding.dateEnds.setText(DateTimeHelper.getScheduleFormattedDate(newDate));
    }

    private void onTimeStartsSet(TimePicker view, int hourOfDay, int minute) {
        Date oldDate = mViewModel.getSelectedDateTimeStarts();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Objects.requireNonNull(oldDate));
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        Date newDate = calendar.getTime();

        mViewModel.setSelectedDateTimeStarts(newDate);
        mBinding.timeStarts.setText(DateTimeHelper.getScheduleFormattedTime(newDate));
    }

    private void onTimeEndsSet(TimePicker view, int hourOfDay, int minute) {
        Date oldDate = mViewModel.getSelectedDateTimeEnds();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Objects.requireNonNull(oldDate));
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        Date newDate = calendar.getTime();

        mViewModel.setSelectedDateTimeEnds(newDate);
        mBinding.timeEnds.setText(DateTimeHelper.getScheduleFormattedTime(newDate));
    }

    private boolean isEditing() {
        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt(Constants.EVENT_ID_INTENT_KEY);
        return id != 0;
        //return bundle != null;
    }

    private void loadEventFromDb(int eventId) {
        mViewModel.getEvent(eventId).observe(this, this);
    }

    private void loadCategoriesFromDb() {
        mViewModel.getAllCategories().observe(this,
                categories -> mViewModel.setCategoriesCache(categories));
    }

    private void showSnackbar(@StringRes int stringResourceId) {
        Snackbar.make(mBinding.activityEventDetailsRoot, stringResourceId, 3000).show();
    }

    private void setSelectedDateTimes(long millis) {
        Date selectedDateTime = new Date(millis);

        Calendar calendarStarts = Calendar.getInstance();
        calendarStarts.setTime(selectedDateTime);

        Calendar calendarEnds = (Calendar) calendarStarts.clone();
        calendarEnds.add(Calendar.HOUR_OF_DAY, 1);

        Date starts = calendarStarts.getTime();
        Date ends = calendarEnds.getTime();

        mViewModel.setSelectedDateTimeStarts(starts);
        mViewModel.setSelectedDateTimeEnds(ends);

        mBinding.dateStarts.setText(DateTimeHelper.getScheduleFormattedDate(starts));
        mBinding.timeStarts.setText(DateTimeHelper.getScheduleFormattedTime(starts));
        mBinding.dateEnds.setText(DateTimeHelper.getScheduleFormattedDate(ends));
        mBinding.timeEnds.setText(DateTimeHelper.getScheduleFormattedTime(ends));
    }
}
