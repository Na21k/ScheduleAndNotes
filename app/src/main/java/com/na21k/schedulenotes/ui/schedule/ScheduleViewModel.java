package com.na21k.schedulenotes.ui.schedule;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.helpers.AlarmsHelper;
import com.na21k.schedulenotes.helpers.DateTimeHelper;
import com.na21k.schedulenotes.helpers.EventsHelper;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.CategoriesRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.schedule.ScheduleRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class ScheduleViewModel extends ViewModel {

    @NonNull
    private final MutableRepository<Event> mMutableScheduleRepository;
    @NonNull
    private final ScheduleRepository mScheduleRepository;
    @NonNull
    private final CanSearchRepository<Event> mCanSearchScheduleRepository;
    @NonNull
    private final LiveData<List<Category>> mAllCategories;
    private List<Event> mEventsCache = null;
    private List<Category> mCategoriesCache = null;
    private Date mSelectedDate = null;
    @NonNull
    private final AlarmsHelper mAlarmsHelper;
    @NonNull
    private final EventsHelper mEventsHelper;

    private ScheduleViewModel(
            @NonNull MutableRepository<Event> mutableScheduleRepository,
            @NonNull ScheduleRepository scheduleRepository,
            @NonNull CanSearchRepository<Event> canSearchScheduleRepository,
            @NonNull CategoriesRepository categoriesRepository,
            @NonNull AlarmsHelper alarmsHelper,
            @NonNull EventsHelper eventsHelper
    ) {
        super();

        mMutableScheduleRepository = mutableScheduleRepository;
        mScheduleRepository = scheduleRepository;
        mCanSearchScheduleRepository = canSearchScheduleRepository;

        mAllCategories = categoriesRepository.getAll();

        mAlarmsHelper = alarmsHelper;
        mEventsHelper = eventsHelper;
    }

    public LiveData<List<Event>> getByDate(Date hasStartedBefore, Date hasNotEndedBy) {
        return mScheduleRepository.getByDate(hasStartedBefore, hasNotEndedBy);
    }

    public LiveData<List<Event>> getEventsSearch(String searchQuery) {
        return mCanSearchScheduleRepository.getSearch(searchQuery);
    }

    public LiveData<List<Category>> getAllCategories() {
        return mAllCategories;
    }

    public void createEvent(Event event) {
        mMutableScheduleRepository.add(event);
    }

    public void updateEvent(Event event) {
        mMutableScheduleRepository.update(event);
    }

    public void deleteEvent(Event event) {
        new Thread(() -> {
            mAlarmsHelper.cancelEventNotificationAlarmsBlocking(event.getId());
            mMutableScheduleRepository.delete(event);
        }).start();
    }

    public void postponeToNextDay(Event event) {
        Date newStarts = DateTimeHelper.addDays(event.getDateTimeStarts(), 1);
        Date newStartsDateOnly = DateTimeHelper.truncateToDateOnly(newStarts);

        postponeTo(event, newStartsDateOnly);
    }

    public void postponeToTomorrow(Event event) {
        Date tomorrow = DateTimeHelper.addDays(new Date(), 1);
        Date tomorrowDateOnly = DateTimeHelper.truncateToDateOnly(tomorrow);

        postponeTo(event, tomorrowDateOnly);
    }

    public void postponeTo(Event event, Date dateOnly) {
        mEventsHelper.postponeToAsync(event, dateOnly);
    }

    public List<Event> getEventsCache() {
        return mEventsCache;
    }

    public void setEventsCache(List<Event> eventsCache) {
        mEventsCache = eventsCache;
    }

    public List<Category> getCategoriesCache() {
        return mCategoriesCache;
    }

    public void setCategoriesCache(List<Category> categoriesCache) {
        mCategoriesCache = categoriesCache;
    }

    public Date getSelectedDate() {
        return mSelectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        mSelectedDate = selectedDate;
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final MutableRepository<Event> mMutableScheduleRepository;
        @NonNull
        private final ScheduleRepository mScheduleRepository;
        @NonNull
        private final CanSearchRepository<Event> mCanSearchScheduleRepository;
        @NonNull
        private final CategoriesRepository mCategoriesRepository;
        @NonNull
        private final AlarmsHelper mAlarmsHelper;
        @NonNull
        private final EventsHelper mEventsHelper;

        @Inject
        public Factory(
                @NonNull MutableRepository<Event> mutableScheduleRepository,
                @NonNull ScheduleRepository scheduleRepository,
                @NonNull CanSearchRepository<Event> canSearchScheduleRepository,
                @NonNull CategoriesRepository categoriesRepository,
                @NonNull AlarmsHelper alarmsHelper,
                @NonNull EventsHelper eventsHelper
        ) {
            mMutableScheduleRepository = mutableScheduleRepository;
            mScheduleRepository = scheduleRepository;
            mCanSearchScheduleRepository = canSearchScheduleRepository;
            mCategoriesRepository = categoriesRepository;
            mAlarmsHelper = alarmsHelper;
            mEventsHelper = eventsHelper;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            ScheduleViewModel vm = new ScheduleViewModel(
                    mMutableScheduleRepository, mScheduleRepository, mCanSearchScheduleRepository,
                    mCategoriesRepository,
                    mAlarmsHelper, mEventsHelper
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
