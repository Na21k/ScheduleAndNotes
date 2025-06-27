package com.na21k.schedulenotes.di.modules;

import android.content.Context;

import com.na21k.schedulenotes.data.models.ColorSetModel;
import com.na21k.schedulenotes.helpers.CategoriesHelper;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import javax.inject.Qualifier;

import dagger.Module;
import dagger.Provides;

@Module
public interface CategoriesModule {

    @Provides
    @CategoriesColorSets
    static List<ColorSetModel> provideColorSetModels(Context context) {
        return CategoriesHelper.getCategoriesColorSets(context);
    }

    @Qualifier
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @interface CategoriesColorSets {
    }
}
