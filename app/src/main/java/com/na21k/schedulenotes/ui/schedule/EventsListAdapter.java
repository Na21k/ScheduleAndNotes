package com.na21k.schedulenotes.ui.schedule;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.databinding.ScheduleListItemBinding;
import com.na21k.schedulenotes.exceptions.CouldNotFindColorSetModelException;
import com.na21k.schedulenotes.helpers.CategoriesHelper;
import com.na21k.schedulenotes.helpers.DateTimeHelper;
import com.na21k.schedulenotes.ui.shared.viewHolders.BaseViewHolder;

import java.util.Date;
import java.util.List;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.EventViewHolder> {

    private final boolean mIsNightMode;
    private final OnEventActionRequestedListener mOnEventActionRequestedListener;
    private List<Category> mCategories = null;
    private List<Event> mEvents;
    private Date mSelectedDate;

    public EventsListAdapter(boolean isNightMode,
                             OnEventActionRequestedListener onEventActionRequestedListener) {
        mIsNightMode = isNightMode;
        mOnEventActionRequestedListener = onEventActionRequestedListener;
        setStateRestorationPolicy(StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ScheduleListItemBinding binding = ScheduleListItemBinding
                .inflate(inflater, parent, false);

        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = mEvents.get(position);
        holder.setData(event);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
        //TODO: group events by day time (morning, afternoon, evening, etc)
    }

    @Override
    public int getItemCount() {
        return mEvents != null ? mEvents.size() : 0;
    }

    public void setData(List<Event> events, List<Category> categories, Date selectedDate) {
        mEvents = events;
        mCategories = categories;
        mSelectedDate = selectedDate;
        notifyDataSetChanged();
    }

    public class EventViewHolder extends BaseViewHolder {

        private final ScheduleListItemBinding mBinding;
        private Event mEvent;

        public EventViewHolder(ScheduleListItemBinding binding) {
            super(binding.getRoot(), R.menu.event_long_press_menu, R.string.event_context_menu_title);
            mBinding = binding;

            itemView.setOnClickListener(
                    v -> mOnEventActionRequestedListener.onEventOpenRequested(mEvent));
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);

            if (mEvent.getCategoryId() == null) {
                menu.removeItem(R.id.event_remove_category_menu_item);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.event_duplicate_menu_item:
                    mOnEventActionRequestedListener.onEventDuplicationRequested(mEvent);
                    return true;
                case R.id.event_delete_menu_item:
                    mOnEventActionRequestedListener.onEventDeletionRequested(mEvent);
                    return true;
                case R.id.event_set_category_menu_item:
                    mOnEventActionRequestedListener.onCategorySelectionRequested(mEvent);
                    return true;
                case R.id.event_remove_category_menu_item:
                    mOnEventActionRequestedListener.onRemoveCategoryRequested(mEvent);
                    return true;
                case R.id.event_postpone_to_next_day_menu_item:
                    mOnEventActionRequestedListener.onPostponeToNextDayRequested(mEvent);
                    return true;
                case R.id.event_postpone_to_tomorrow_menu_item:
                    mOnEventActionRequestedListener.onPostponeToTomorrowRequested(mEvent);
                    return true;
                case R.id.event_postpone_to_date_menu_item:
                    mOnEventActionRequestedListener.onPostponeRequested(mEvent);
                    return true;
                default:
                    return false;
            }
        }

        private void setData(@NonNull Event event) throws CouldNotFindColorSetModelException {
            mEvent = event;

            if (event.isHidden()) {
                mBinding.eventTitle.setText(R.string.hidden_event);
            } else {
                mBinding.eventTitle.setText(event.getTitle());
            }

            String startsFormatted = getTimeFormatted(event.getDateTimeStarts());
            String endsFormatted = getTimeFormatted(event.getDateTimeEnds());
            mBinding.eventStartTime.setText(startsFormatted);
            mBinding.eventEndTime.setText(endsFormatted);

            int categoryColor = CategoriesHelper.getCategoryColor(
                    itemView.getContext(), event.getCategoryId(), mCategories, mIsNightMode);
            mBinding.scheduleListItemCard.setStrokeColor(categoryColor);

            if (!mIsNightMode) {
                mBinding.scheduleListItemCard.setCardBackgroundColor(categoryColor);
            }
        }

        private String getTimeFormatted(Date dateTime) {
            Date dateOnly = DateTimeHelper.truncateToDateOnly(dateTime);

            if (dateOnly.equals(mSelectedDate)) {
                return DateTimeHelper.getScheduleFormattedTime(dateTime);
            } else {
                return DateTimeHelper.getScheduleShortFormattedDate(dateTime);
            }
        }
    }

    public interface OnEventActionRequestedListener {

        void onEventOpenRequested(Event event);

        void onEventDuplicationRequested(Event event);

        void onEventDeletionRequested(Event event);

        void onCategorySelectionRequested(Event event);

        void onRemoveCategoryRequested(Event event);

        void onPostponeToNextDayRequested(Event event);

        void onPostponeToTomorrowRequested(Event event);

        void onPostponeRequested(Event event);
    }
}
