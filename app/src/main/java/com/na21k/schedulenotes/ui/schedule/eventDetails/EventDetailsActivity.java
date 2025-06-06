package com.na21k.schedulenotes.ui.schedule.eventDetails;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.databinding.ActivityEventDetailsBinding;
import com.na21k.schedulenotes.helpers.UiHelper;
import com.na21k.schedulenotes.ui.categories.categoryDetails.CategoryDetailsActivity;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Pass data using a Bundle.</br>
 * </br>
 * Operation modes:</br>
 * 1. Create an event - pass time millis (long) (for the initial event date/time value);</br>
 * 2. Duplicate an event - pass the event data as an {@link Event} object
 * (a copy will be created);</br>
 * 3. Edit an event - pass the event id.</br>
 *
 * @implNote Passing data for different operation modes at once (for example,
 * passing an event id, as for editing, along with an {@link Event} object, as for duplicating)
 * causes the following <b><u>operation mode priority</u></b> to be used: edit > duplicate > create.
 * @see Constants
 */
public class EventDetailsActivity extends AppCompatActivity implements Observer<Event> {

    private enum OperationMode {Create, Duplicate, Edit}

    public static final String DUPLICATE_EVENT_DATA_INTENT_KEY = "duplicateEventDataIntentKey";

    private EventDetailsViewModel mViewModel;
    private ActivityEventDetailsBinding mBinding;
    private Integer mCurrentEventsCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);
        mBinding = ActivityEventDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar.appBar);

        handleWindowInsets();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();

        switch (getOperationMode()) {
            case Create:
                setTitle(R.string.title_create_event);

                long millis = bundle.getLong(Constants.SELECTED_TIME_MILLIS_INTENT_KEY);
                setSelectedDateTimes(millis);

                break;
            case Duplicate:
                setTitle(R.string.title_create_event);

                Event event = (Event) bundle.getSerializable(DUPLICATE_EVENT_DATA_INTENT_KEY);
                prepareForDuplication(new Event(event));

                break;
            case Edit:
                setTitle(R.string.title_edit_event);

                int eventId = bundle.getInt(Constants.EVENT_ID_INTENT_KEY);
                loadEventFromDb(eventId);

                break;
        }

        loadCategoriesFromDb();
    }

    @Override
    public void onChanged(Event event) {
        mBinding.eventTitle.setText(event.getTitle());
        mBinding.eventDetails.setText(event.getDetails());
        mBinding.isHidden.setChecked(event.isHidden());
        mBinding.dateTimeStartsEndsPicker.setSelectedDateTimeStarts(event.getDateTimeStarts());
        mBinding.dateTimeStartsEndsPicker.setSelectedDateTimeEnds(event.getDateTimeEnds());

        mCurrentEventsCategoryId = event.getCategoryId();
        invalidateOptionsMenu();    //event category might have changed
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.event_details_menu, menu);

        if (getOperationMode() != OperationMode.Edit) {
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

    private void handleWindowInsets() {
        UiHelper.handleWindowInsets(getWindow(), mBinding.getRoot(),
                mBinding.container, mBinding.scrollableContent, null, true);
    }

    private void saveEvent() {
        Editable titleEditable = mBinding.eventTitle.getText();
        Editable detailsEditable = mBinding.eventDetails.getText();
        boolean isHidden = mBinding.isHidden.isChecked();
        Date starts = mBinding.dateTimeStartsEndsPicker.getSelectedDateTimeStarts();
        Date ends = mBinding.dateTimeStartsEndsPicker.getSelectedDateTimeEnds();

        if (titleEditable == null || titleEditable.toString().isEmpty()) {
            UiHelper.showSnackbar(
                    mBinding.getRoot(), R.string.specify_event_title_snackbar);

            return;
        }

        if (starts.compareTo(ends) > 0) {
            UiHelper.showSnackbar(
                    mBinding.getRoot(), R.string.start_time_less_than_end_time_snackbar);

            return;
        }

        Event event = new Event(0, titleEditable.toString(), detailsEditable.toString(),
                mCurrentEventsCategoryId, starts, ends, isHidden);

        if (getOperationMode() == OperationMode.Edit) {
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
        UiHelper.showSnackbar(mBinding.getRoot(), R.string.excluded_from_category_snackbar);

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
                UiHelper.showSnackbar(mBinding.getRoot(), R.string.category_set_snackbar);
                invalidateOptionsMenu();    //show the Exclude from category button
            });

            builder.show();
        }
    }

    private OperationMode getOperationMode() {
        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            throw new IllegalArgumentException("No Bundle was passed, see the class' Javadoc");
        }

        if (bundle.getInt(Constants.EVENT_ID_INTENT_KEY) != 0) {
            return OperationMode.Edit;
        }
        if (bundle.getSerializable(DUPLICATE_EVENT_DATA_INTENT_KEY) != null) {
            return OperationMode.Duplicate;
        }

        return OperationMode.Create;
    }

    private void loadEventFromDb(int eventId) {
        mViewModel.getEvent(eventId).observe(this, this);
    }

    private void loadCategoriesFromDb() {
        mViewModel.getAllCategories().observe(this,
                categories -> mViewModel.setCategoriesCache(categories));
    }

    private void prepareForDuplication(Event event) {
        mBinding.duplicationWarning.setVisibility(View.VISIBLE);
        mBinding.eventTitle.setText(event.getTitle());
        mBinding.eventDetails.setText(event.getDetails());
        mBinding.isHidden.setChecked(event.isHidden());
        mBinding.dateTimeStartsEndsPicker.setSelectedDateTimeStarts(event.getDateTimeStarts());
        mBinding.dateTimeStartsEndsPicker.setSelectedDateTimeEnds(event.getDateTimeEnds());

        mCurrentEventsCategoryId = event.getCategoryId();
        invalidateOptionsMenu();    //event category might have changed
    }

    private void setSelectedDateTimes(long millis) {
        Date selectedDateTime = new Date(millis);

        Calendar calendarStarts = Calendar.getInstance();
        calendarStarts.setTime(selectedDateTime);

        Calendar calendarEnds = (Calendar) calendarStarts.clone();
        calendarEnds.add(Calendar.HOUR_OF_DAY, 1);

        Date starts = calendarStarts.getTime();
        Date ends = calendarEnds.getTime();

        mBinding.dateTimeStartsEndsPicker.setSelectedDateTimeStarts(starts);
        mBinding.dateTimeStartsEndsPicker.setSelectedDateTimeEnds(ends);
    }
}
