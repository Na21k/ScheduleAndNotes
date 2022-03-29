package com.na21k.schedulenotes.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.na21k.schedulenotes.R;

public class UiHelper {

    public static boolean isInDarkMode(@NonNull Fragment fragment) {
        return (fragment.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }

    public static boolean isInDarkMode(@NonNull AppCompatActivity activity) {
        return (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }

    public static boolean isTablet(@NonNull Resources resources) {
        return (resources.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void showErrorDialog(Context context, @StringRes int errorTextResource) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.ic_error_24);
        builder.setTitle(R.string.oops_aletr_title);
        builder.setMessage(errorTextResource);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
        });

        builder.show();
    }
}
