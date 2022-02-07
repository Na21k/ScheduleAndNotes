package com.na21k.schedulenotes.ui.settings;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Schedule.EventDao;
import com.na21k.schedulenotes.ui.settings.importExport.ExportActivity;
import com.na21k.schedulenotes.ui.settings.importExport.ImportActivity;

import java.util.Calendar;
import java.util.Date;

public class SettingsFragment extends PreferenceFragmentCompat implements DatePickerDialog.OnDateSetListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        final String IMPORT_PREFERENCE_KEY = "import";
        final String EXPORT_PREFERENCE_KEY = "export";
        final String DELETE_OLDER_THAN_PREFERENCE_KEY = "delete_older_than";

        switch (preference.getKey()) {
            case IMPORT_PREFERENCE_KEY:
                importData();
                return true;
            case EXPORT_PREFERENCE_KEY:
                exportData();
                return true;
            case DELETE_OLDER_THAN_PREFERENCE_KEY:
                deleteOld();
                return true;
            default:
                return super.onPreferenceTreeClick(preference);
        }
    }

    private void importData() {
        Context context = getContext();

        if (context != null) {
            Intent importActivityIntent = new Intent(context, ImportActivity.class);
            startActivity(importActivityIntent);
        }
    }

    private void exportData() {
        Context context = getContext();

        if (context != null) {
            Intent exportActivityIntent = new Intent(context, ExportActivity.class);
            startActivity(exportActivityIntent);
        }
    }

    private void deleteOld() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), this, year, month, day);
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, dayOfMonth, 23, 59, 59);
        Date selectedDate = calendar.getTime();

        deleteOlderThanInclusive(selectedDate);
    }

    private void deleteOlderThanInclusive(Date date) {
        AppDatabase db = AppDatabase.getInstance(getContext());
        EventDao events = db.eventDao();

        Thread thread = new Thread(() -> {
            events.deleteOlderThan(date);

            showSnackbar(R.string.delete_events_older_than_succeeded_snackbar);
        });
        thread.start();
    }

    private void showSnackbar(int textResourceId) {
        Activity activity = getActivity();

        if (activity != null) {
            View snackView = activity.findViewById(R.id.nav_host_fragment_activity_main);
            Snackbar snackbar = Snackbar.make(snackView, textResourceId, 5000);
            snackbar.setAnchorView(R.id.nav_view);
            //snackbar.setAction("OK", v -> snackbar.dismiss());

            snackbar.show();
        }
    }
}
