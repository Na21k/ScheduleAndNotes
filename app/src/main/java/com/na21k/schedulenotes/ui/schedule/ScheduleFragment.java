package com.na21k.schedulenotes.ui.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.helpers.UiHelper;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.databinding.ScheduleFragmentBinding;
import com.na21k.schedulenotes.ui.schedule.eventDetails.EventDetailsActivity;

import java.util.Comparator;
import java.util.List;

public class ScheduleFragment extends Fragment implements EventsListAdapter.OnEventActionRequestedListener {

    private ScheduleViewModel mViewModel;
    private ScheduleFragmentBinding mBinding;

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
        EventsListAdapter adapter = setUpRecyclerView();
        setObservers(adapter);
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

    private void setObservers(EventsListAdapter adapter) {
        mViewModel.getAllEvents().observe(getViewLifecycleOwner(), events -> {
            events.sort(Comparator.comparing(Event::getDateTimeStarts));
            mViewModel.setEventsCache(events);
            updateListIfEnoughData(adapter);
        });

        mViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            mViewModel.setCategoriesCache(categories);
            updateListIfEnoughData(adapter);
        });
    }

    private void updateListIfEnoughData(EventsListAdapter adapter) {
        List<Event> eventsCache = mViewModel.getEventsCache();
        List<Category> categoriesCache = mViewModel.getCategoriesCache();
        boolean isEnoughData = eventsCache != null && categoriesCache != null;

        if (!isEnoughData) {
            return;
        }

        adapter.setEvents(eventsCache, categoriesCache);
    }

    private void setListeners() {
        mBinding.calendarBtnCard.setOnLongClickListener(v -> jumpToToday());

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

    private boolean jumpToToday() {
        return true;
    }

    private void newEvent() {
        Context context = getContext();

        if (context != null) {
            Intent intent = new Intent(context, EventDetailsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onCategorySelectionRequested(Event event) {

    }

    @Override
    public void onEventDeletionRequested(Event event) {

    }

    @Override
    public void onRemoveCategoryRequested(Event event) {
        event.setCategoryId(null);
        mViewModel.updateEvent(event);
    }
}
