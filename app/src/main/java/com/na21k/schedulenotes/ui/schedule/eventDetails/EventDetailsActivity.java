package com.na21k.schedulenotes.ui.schedule.eventDetails;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

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

import java.util.Comparator;

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

        if (isEditing()) {
            setTitle("Edit event");

            Bundle bundle = getIntent().getExtras();
            int eventId = bundle.getInt(Constants.EVENT_ID_INTENT_KEY);
            loadEventFromDb(eventId);
        } else {
            setTitle("Create event");
        }
    }

    @Override
    public void onChanged(Event event) {

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

        mViewModel.getAllCategories().observe(this, categories -> {
            categories.sort(Comparator.comparing(Category::getTitle));

            ArrayAdapter<Category> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, categories);
            builder.setAdapter(adapter, (dialog, which) -> {
                mCurrentEventsCategoryId = categories.get(which).getId();
                showSnackbar(R.string.category_set_snackbar);
                invalidateOptionsMenu();    //show the Exclude from category button
            });

            builder.show();
        });
    }

    private boolean isEditing() {
        Bundle bundle = getIntent().getExtras();
        return bundle != null;
    }

    private void loadEventFromDb(int eventId) {
        mViewModel.getEvent(eventId).observe(this, this);
    }

    private void showSnackbar(@StringRes int stringResourceId) {
        Snackbar.make(mBinding.activityEventDetailsRoot, stringResourceId, 3000).show();
    }
}
