package com.na21k.schedulenotes.helpers;

import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class UiHelper {

    public static boolean isInDarkMode(@NonNull Fragment fragment) {
        return (fragment.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }

    public static boolean isInDarkMode(@NonNull AppCompatActivity activity) {
        return (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }
}
