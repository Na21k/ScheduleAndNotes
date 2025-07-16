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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.ScheduleNotesApplication;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.databinding.ActivityEventDetailsBinding;
import com.na21k.schedulenotes.helpers.UiHelper;
import com.na21k.schedulenotes.ui.categories.categoryDetails.CategoryDetailsActivity;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 * Pass data using a Bundle.</br>
 * </br>
 * Operation modes:</br>
 * 1. Create an event - pass time millis (long) (for the initial event date/time value);</br>
 * 2. Duplicate an event - pass the event data as an {@link Event} object
 * (use the constant in this class for the Bundle key);</br>
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

    private EventDetailsViewModel.Factory mViewModelFactory;
    private EventDetailsViewModel mViewModel;
    private ActivityEventDetailsBinding mBinding;
    private List<Category> mCategoriesLatest;
    private Integer mCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((ScheduleNotesApplication) getApplicationContext())
                .getAppComponent()
                .inject(this);
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, mViewModelFactory)
                .get(EventDetailsViewModel.class);
        mBinding = ActivityEventDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar.appBar);

        handleWindowInsets();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getOperationMode() == OperationMode.Edit
                ? R.string.title_edit_event
                : R.string.title_create_event);

        switch (getOperationMode()) {
            case Create:
                prepareForCreation();
                break;
            case Duplicate:
                prepareForDuplication();
                break;
            case Edit:
                observeEvent();
                break;
        }

        observeCategories();
    }

    @Inject
    protected void initViewModelFactory(
            EventDetailsViewModel.Factory.AssistedFactory viewModelFactoryAssistedFactory
    ) {
        int eventId = 0;
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            eventId = bundle.getInt(Constants.EVENT_ID_INTENT_KEY);
        }

        mViewModelFactory = viewModelFactoryAssistedFactory.create(eventId);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.event_details_menu, menu);

        if (getOperationMode() != OperationMode.Edit) {
            menu.removeItem(R.id.menu_delete);
        }

        if (mCategoryId == null) {
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

    @Override
    public void onChanged(Event event) {
        if (event == null) {
            //ignore during deletion
            return;
        }

        setUiValuesFromEvent(event);
    }

    private void observeEvent() {
        mViewModel.getEvent().observe(this, this);
    }

    private void observeCategories() {
        mViewModel.getCategories().observe(this,
                categories -> mCategoriesLatest = categories);
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
                mCategoryId, starts, ends, isHidden);

        mViewModel.saveEvent(event);
        finish();
    }

    private void deleteEvent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_24);
        builder.setTitle(R.string.event_deletion_alert_title);
        builder.setMessage(R.string.event_deletion_alert_message);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            mViewModel.deleteEvent();
            finish();
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }

    private void removeCategory() {
        mCategoryId = null;
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

        if (mCategoriesLatest != null) {
            mCategoriesLatest.sort(Comparator.comparing(Category::getTitle));

            ArrayAdapter<Category> adapter = new ArrayAdapter<>(EventDetailsActivity.this,
                    android.R.layout.simple_list_item_1, mCategoriesLatest);
            builder.setAdapter(adapter, (dialog, which) -> {
                mCategoryId = mCategoriesLatest.get(which).getId();
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

    private void prepareForDuplication() {
        if (getOperationMode() != OperationMode.Duplicate) {
            throw new IllegalStateException("Inappropriate " + OperationMode.class.getName());
        }

        Bundle bundle = getIntent().getExtras();
        Event event = (Event) bundle.getSerializable(DUPLICATE_EVENT_DATA_INTENT_KEY);

        mBinding.duplicationWarning.setVisibility(View.VISIBLE);
        setUiValuesFromEvent(event);
    }

    private void setUiValuesFromEvent(Event event) {
        mBinding.eventTitle.setText(event.getTitle());
        mBinding.eventDetails.setText(event.getDetails());
        mBinding.isHidden.setChecked(event.isHidden());
        mBinding.dateTimeStartsEndsPicker.setSelectedDateTimeStarts(event.getDateTimeStarts());
        mBinding.dateTimeStartsEndsPicker.setSelectedDateTimeEnds(event.getDateTimeEnds());

        mCategoryId = event.getCategoryId();
        invalidateOptionsMenu();    //event category might have changed
    }

    private void prepareForCreation() {
        if (getOperationMode() != OperationMode.Create) {
            throw new IllegalStateException("Inappropriate " + OperationMode.class.getName());
        }

        Bundle bundle = getIntent().getExtras();
        long millis = bundle.getLong(Constants.SELECTED_TIME_MILLIS_INTENT_KEY);

        setSelectedDateTimes(millis);
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
