package com.na21k.schedulenotes;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public class ScheduleNotesAppGlideModule extends AppGlideModule {

    private static final String DISK_CACHE_NAME = "glide_disk_cache";
    private static final int DISK_CACHE_SIZE = 150 * 1024 * 1024;   //150 MiB
    private static final int TRANSITION_DURATION = 130;

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        builder
                .setDefaultTransitionOptions(
                        Drawable.class,
                        DrawableTransitionOptions.withCrossFade(TRANSITION_DURATION)
                )
                .setDiskCache(
                        new InternalCacheDiskCacheFactory(
                                context,
                                DISK_CACHE_NAME,
                                DISK_CACHE_SIZE
                        )
                );
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
