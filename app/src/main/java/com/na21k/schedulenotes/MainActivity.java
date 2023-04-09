package com.na21k.schedulenotes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.BackoffPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.na21k.schedulenotes.databinding.ActivityMainBinding;
import com.na21k.schedulenotes.helpers.DateTimeHelper;
import com.na21k.schedulenotes.helpers.EventsHelper;
import com.na21k.schedulenotes.helpers.NotificationsHelper;
import com.na21k.schedulenotes.ui.settings.SettingsActivity;
import com.na21k.schedulenotes.workers.RecommendationsWorker;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar.appBar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_schedule, R.id.navigation_notes, R.id.navigation_lists,
                R.id.navigation_categories).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(mBinding.navView, navController);

        makeNavBarLookNice();

        requestNotificationsPermission();

        createNotificationChannels();
        ensureEventNotificationsScheduledAsync();
        ensureRecommendationsWorkerIsScheduled();
    }

    private void makeNavBarLookNice() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.getRoot(), (v, insets) -> {
            Insets i = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            mBinding.container.setPadding(i.left, i.top, i.right, 0);
            mBinding.navView.setPadding(0, 0, 0, i.bottom);

            return WindowInsetsCompat.CONSUMED;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void ensureEventNotificationsScheduledAsync() {
        new Thread(() -> EventsHelper.ensureEventNotificationsScheduledBlocking(this))
                .start();
    }

    private void ensureRecommendationsWorkerIsScheduled() {
        WorkManager.getInstance(this)
                .cancelAllWorkByTag(Constants.RECOMMENDATIONS_WORKER_TAG);

        Date now = new Date();
        Date notifyTime = Constants.getRecommendationsTime();
        boolean notifyToday = notifyTime.after(now);

        if (!notifyToday) {
            //schedule the nearest notification for tomorrow
            notifyTime = DateTimeHelper.addDays(notifyTime, 1);
        }

        long delay = notifyTime.getTime() - now.getTime();

        PeriodicWorkRequest recommendationsNotificationsWorkRequest =
                getRecommendationsWorkRequestBuilder(delay).build();

        WorkManager manager = WorkManager.getInstance(this);
        manager.enqueue(recommendationsNotificationsWorkRequest);
    }

    @NonNull
    private static PeriodicWorkRequest.Builder getRecommendationsWorkRequestBuilder(
            long delayMillis) {
        return new PeriodicWorkRequest.Builder(
                RecommendationsWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .addTag(Constants.RECOMMENDATIONS_WORKER_TAG);
    }

    private void createNotificationChannels() {
        NotificationsHelper.addEventsNotificationChannel(this);
        NotificationsHelper.addMoviesNotificationChannel(this);
        NotificationsHelper.addMusicNotificationChannel(this);
        NotificationsHelper.addLanguagesListNotificationChannel(this);
    }

    private void requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat
                        .checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityResultLauncher<String> requestNotificationsPermissionLauncher =
                    registerForActivityResult(
                            new ActivityResultContracts.RequestPermission(), isGranted -> {
                            });

            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.ic_notifications_24);
                builder.setTitle(R.string.notifications_permission_request_alert_title);
                builder.setMessage(R.string.notifications_permission_request_alert_message);

                builder.setPositiveButton(android.R.string.ok, (dialog, which) ->
                        requestNotificationsPermissionLauncher
                                .launch(Manifest.permission.POST_NOTIFICATIONS));

                builder.show();
            } else {
                requestNotificationsPermissionLauncher
                        .launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}
