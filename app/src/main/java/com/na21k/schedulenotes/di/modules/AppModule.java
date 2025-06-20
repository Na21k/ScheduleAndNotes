package com.na21k.schedulenotes.di.modules;

import android.app.Application;
import android.content.Context;

import dagger.Binds;
import dagger.Module;

@Module
public interface AppModule {

    @Binds
    Context applicationContext(Application application);
}
