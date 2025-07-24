package com.na21k.schedulenotes;

import android.app.Application;

import com.na21k.schedulenotes.di.components.ApplicationComponent;
import com.na21k.schedulenotes.di.components.DaggerApplicationComponent;

public class ScheduleNotesApplication extends Application {

    private ApplicationComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerApplicationComponent
                .factory()
                .create(this);
    }

    public ApplicationComponent getAppComponent() {
        return appComponent;
    }
}
