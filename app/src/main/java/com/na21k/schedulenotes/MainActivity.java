package com.na21k.schedulenotes;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.BackoffPolicy;
import androidx.work.ListenableWorker;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.na21k.schedulenotes.databinding.ActivityMainBinding;
import com.na21k.schedulenotes.helpers.DateTimeHelper;
import com.na21k.schedulenotes.helpers.NotificationsHelper;
import com.na21k.schedulenotes.helpers.UiHelper;
import com.na21k.schedulenotes.workers.MovieNotificationWorker;
import com.na21k.schedulenotes.workers.MusicNotificationWorker;
import com.na21k.schedulenotes.workers.WordOrPhraseNotificationWorker;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_schedule, R.id.navigation_notes, R.id.navigation_lists,
                R.id.navigation_categories, R.id.navigation_settings/*, R.id.navigation_home*/)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (!UiHelper.isInDarkMode(this)) {
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }

        createNotificationChannels();
        ensureRecommendationsWorkerIsScheduled();
        showTestNotification();
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

        WorkRequest movieNotificationWorkRequest = getMovieNotificationWorkRequest(delay);
        WorkRequest musicNotificationWorkRequest = getMusicNotificationWorkRequest(delay);
        WorkRequest wordNotificationWorkRequest = getWordOrPhraseNotificationWorkRequest(delay);

        WorkManager manager = WorkManager.getInstance(this);

        manager.enqueue(movieNotificationWorkRequest);
        manager.enqueue(musicNotificationWorkRequest);
        manager.enqueue(wordNotificationWorkRequest);
    }

    @NonNull
    private static WorkRequest getMovieNotificationWorkRequest(long delay) {
        return getRecommendationsWorkRequestBuilderForWorker(MovieNotificationWorker.class, delay)
                .build();
    }

    @NonNull
    private static WorkRequest getMusicNotificationWorkRequest(long delay) {
        return getRecommendationsWorkRequestBuilderForWorker(MusicNotificationWorker.class, delay)
                .build();
    }

    @NonNull
    private static WorkRequest getWordOrPhraseNotificationWorkRequest(long delay) {
        return getRecommendationsWorkRequestBuilderForWorker(WordOrPhraseNotificationWorker.class,
                delay)
                .build();
    }

    @NonNull
    private static PeriodicWorkRequest.Builder getRecommendationsWorkRequestBuilderForWorker(
            @NonNull Class<? extends ListenableWorker> workerClass, long delayMillis) {
        return new PeriodicWorkRequest.Builder(workerClass,
                1, TimeUnit.DAYS,
                1, TimeUnit.HOURS)
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .addTag(Constants.RECOMMENDATIONS_WORKER_TAG);
    }

    private void showTestNotification() {
        /*Data inputData = new Data.Builder()
                .putInt(TestNotificationWorker.EVENT_ID_INPUT_DATA_KEY, 1).build();

        WorkRequest request = new OneTimeWorkRequest
                .Builder(TestNotificationWorker.class)
                .setInputData(inputData)
                .setInitialDelay(2, TimeUnit.MINUTES)
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .build();

        String requestId = request.getId().toString();

        WorkManager.getInstance(this).enqueue(request);*/
        /*Data inputData = new Data.Builder()
                .putInt(EventNotificationWorker.EVENT_ID_INPUT_DATA_KEY, 12)
                .build();

        WorkRequest request = new OneTimeWorkRequest
                .Builder(EventNotificationWorker.class)
                .setInputData(inputData)
                .setInitialDelay(3, TimeUnit.SECONDS)
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .build();

        String requestId = request.getId().toString();

        WorkManager.getInstance(this).enqueue(request);*/
    }

    private void createNotificationChannels() {
        NotificationsHelper.addTestNotificationChannel(this);
        NotificationsHelper.addEventsNotificationChannel(this);
        NotificationsHelper.addMoviesNotificationChannel(this);
        NotificationsHelper.addMusicNotificationChannel(this);
        NotificationsHelper.addLanguagesListNotificationChannel(this);
    }
}
