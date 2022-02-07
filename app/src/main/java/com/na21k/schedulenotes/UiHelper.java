package com.na21k.schedulenotes;

import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class UiHelper {

    public static boolean isInDarkMode(@NonNull Fragment fragment) {
        return (fragment.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }
}
