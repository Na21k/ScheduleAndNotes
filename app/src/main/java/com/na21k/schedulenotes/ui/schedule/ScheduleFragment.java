package com.na21k.schedulenotes.ui.schedule;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.databinding.ScheduleFragmentBinding;
import com.na21k.schedulenotes.helpers.DateTimeHelper;
import com.na21k.schedulenotes.helpers.UiHelper;
import com.na21k.schedulenotes.ui.schedule.eventDetails.EventDetailsActivity;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ScheduleFragment extends Fragment
        implements Observer<List<Event>>, EventsListAdapter.OnEventActionRequestedListener {

    private ScheduleViewModel mViewModel;
    private ScheduleFragmentBinding mBinding;
    private EventsListAdapter mAdapter;
    private LiveData<List<Event>> mScheduleListLiveData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = ScheduleFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = setUpRecyclerView();
        setObservers();
        setListeners();
    }

    private EventsListAdapter setUpRecyclerView() {
        RecyclerView recyclerView = mBinding.includedList.scheduleList;
        EventsListAdapter adapter =
                new EventsListAdapter(UiHelper.isInDarkMode(this), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return adapter;
    }

    private void setObservers() {
        mViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            mViewModel.setCategoriesCache(categories);
            updateListIfEnoughData();
        });

        jumpToToday(true);
    }

    private void updateListIfEnoughData() {
        List<Event> eventsCache = mViewModel.getEventsCache();
        List<Category> categoriesCache = mViewModel.getCategoriesCache();
        boolean isEnoughData = eventsCache != null && categoriesCache != null;

        if (!isEnoughData) {
            return;
        }

        mAdapter.setEvents(eventsCache, categoriesCache);
        updateSelectedDateText();
    }

    private void updateSelectedDateText() {
        Date date = mViewModel.getSelectedDate();
        String dateFormatted = DateTimeHelper.getScheduleFormattedDate(date);

        mBinding.calendarBtnText.setText(dateFormatted);
    }

    private boolean jumpToToday(boolean isSilentLoad) {
        Date today = new Date();
        jumpToDate(today);

        if (!isSilentLoad) {
            showSnackbar(R.string.jump_to_today_snackbar);
        }

        return true;
    }

    private void jumpToNextDay() {
        Date selected = mViewModel.getSelectedDate();
        Date nxt = DateTimeHelper.addDays(selected, 1);
        jumpToDate(nxt);
    }

    private void jumpToPrevDay() {
        Date selected = mViewModel.getSelectedDate();
        Date prev = DateTimeHelper.addDays(selected, -1);
        jumpToDate(prev);
    }

    private void jumpToDate(Date date) {
        date = DateTimeHelper.truncateToDateOnly(date);
        mViewModel.setSelectedDate(date);

        Date hasStartedBefore = DateTimeHelper.addDays(date, 1);
        Date hasNotEndedBy = date;

        unsubscribeFromLiveData();
        mScheduleListLiveData = mViewModel.getByDate(hasStartedBefore, hasNotEndedBy);
        mScheduleListLiveData.observe(getViewLifecycleOwner(), this);
    }

    @Override
    public void onChanged(List<Event> events) {
        events.sort(Comparator.comparing(Event::getDateTimeStarts));
        mViewModel.setEventsCache(events);
        updateListIfEnoughData();
    }

    private void unsubscribeFromLiveData() {
        if (mScheduleListLiveData != null) {
            mScheduleListLiveData.removeObserver(this);
        }
    }

    private void displayDateSelection() {
        createDatePicker(this::onDateSelected).show();
    }

    private DatePickerDialog createDatePicker(DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mViewModel.getSelectedDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getContext(), dateSetListener, year, month, day);
    }

    private void onDateSelected(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        Date date = calendar.getTime();

        jumpToDate(date);
    }

    private void setListeners() {
        mBinding.calendarBtnCard.setOnLongClickListener(v -> jumpToToday(false));
        mBinding.calendarBtnCard.setOnClickListener(v -> displayDateSelection());
        mBinding.nextDateBtn.setOnClickListener(v -> jumpToNextDay());
        mBinding.previousDateBtn.setOnClickListener(v -> jumpToPrevDay());

        mBinding.addEventFab.setOnClickListener(v -> newEvent());

        mBinding.includedList.scheduleList.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY <= oldScrollY) {
                        mBinding.addEventFab.extend();
                    } else {
                        mBinding.addEventFab.shrink();
                    }

                    if (!v.canScrollVertically(1) && v.canScrollVertically(-1)) {
                        mBinding.addEventFab.hide();
                    } else {
                        mBinding.addEventFab.show();
                    }
                });
    }

    private void newEvent() {
        Context context = getContext();

        if (context != null) {
            Intent intent = new Intent(context, EventDetailsActivity.class);

            Bundle bundle = new Bundle();
            Date date = mViewModel.getSelectedDate();
            Date time = DateTimeHelper.getTimeOnly(new Date());
            long millis = date.getTime() + time.getTime();
            bundle.putLong(Constants.SELECTED_TIME_MILLIS_INTENT_KEY, millis);
            intent.putExtras(bundle);

            startActivity(intent);
        }
    }

    @Override
    public void onCategorySelectionRequested(Event event) {
        Context context = getContext();

        if (context != null) {
            List<Category> categoriesCache = mViewModel.getCategoriesCache();
            categoriesCache.sort(Comparator.comparing(Category::getTitle));

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.ic_categories_24);
            builder.setTitle(R.string.pick_category_dialog_title);

            ArrayAdapter<Category> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_list_item_1, categoriesCache);
            builder.setAdapter(adapter, (dialog, which) -> {
                int selectedCategoryId = categoriesCache.get(which).getId();
                event.setCategoryId(selectedCategoryId);
                mViewModel.updateEvent(event);
            });

            builder.show();
        }
    }

    @Override
    public void onEventDeletionRequested(Event event) {
        Context context = getContext();

        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.ic_delete_24);
            builder.setTitle(R.string.event_deletion_alert_title);
            builder.setMessage(R.string.event_deletion_alert_message);

            builder.setPositiveButton(R.string.delete, (dialog, which) -> {
                mViewModel.deleteEvent(event);
                showSnackbar(R.string.event_deleted_snackbar);
            });
            builder.setNegativeButton(R.string.keep, (dialog, which) -> {
            });

            builder.show();
        }
    }

    @Override
    public void onRemoveCategoryRequested(Event event) {
        event.setCategoryId(null);
        mViewModel.updateEvent(event);
    }

    private void showSnackbar(@StringRes int stringResourceId) {
        Snackbar.make(mBinding.scheduleFragmentRoot, stringResourceId, 3000).show();
    }
}
