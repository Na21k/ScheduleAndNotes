package com.na21k.schedulenotes.di.modules;

import android.content.Context;

import com.na21k.schedulenotes.Constants;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

import javax.inject.Qualifier;

import dagger.Module;
import dagger.Provides;

@Module
public interface FilePathsModule {

    @Provides
    @LanguagesListAttachedImagesAbsoluteFolderPath
    static String provideLanguagesListAttachedImagesAbsoluteFolderPath(Context context) {
        return String.format(
                Locale.US,
                "%s/%s",
                context.getFilesDir().getAbsolutePath(),
                Constants.LANGUAGES_LIST_ATTACHED_IMAGES_LOCATION_RELATIVE
        );
    }

    @Provides
    @LanguagesListAttachedImagesSelectedForAdditionTmpFolderPath
    static String provideLanguagesListAttachedImagesSelectedForAdditionTmpFolderPath(Context context) {
        return String.format(
                Locale.US,
                "%s/%s",
                context.getCacheDir().getAbsolutePath(),
                "attachmentsSelectedForAdditionTmpDir"
        );
    }

    @Qualifier
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @interface LanguagesListAttachedImagesAbsoluteFolderPath {
    }

    @Qualifier
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @interface LanguagesListAttachedImagesSelectedForAdditionTmpFolderPath {
    }
}
