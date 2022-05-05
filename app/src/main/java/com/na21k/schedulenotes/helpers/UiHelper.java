package com.na21k.schedulenotes.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;

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

    @NonNull
    public static LinearLayoutManager getRecyclerViewLayoutManager(@NonNull Context context,
                                                                   int landscapeColumnCountTablet,
                                                                   int portraitColumnCountTablet,
                                                                   int landscapeColumnCount) {
        return getRecyclerViewLayoutManager(context, landscapeColumnCountTablet,
                portraitColumnCountTablet, landscapeColumnCount,
                null, null);
    }

    @NonNull
    public static LinearLayoutManager getRecyclerViewLayoutManager(@NonNull Context context,
                                                                   int landscapeColumnCountTablet,
                                                                   int portraitColumnCountTablet,
                                                                   int landscapeColumnCount,
                                                                   @Nullable Integer headerViewType,
                                                                   @Nullable Adapter viewTypeSupplier) {
        Resources resources = context.getResources();
        int orientation = resources.getConfiguration().orientation;
        boolean isTablet = isTablet(resources);

        if (!isTablet && orientation != Configuration.ORIENTATION_LANDSCAPE) {
            return new LinearLayoutManager(context);
        }

        int columnCount;

        if (isTablet) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                columnCount = landscapeColumnCountTablet;
            } else {
                columnCount = portraitColumnCountTablet;
            }
        } else {
            columnCount = landscapeColumnCount;
        }

        GridLayoutManager layoutManager = new GridLayoutManager(context, columnCount);

        if (headerViewType != null && viewTypeSupplier != null) {
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = viewTypeSupplier.getItemViewType(position);

                    if (viewType == headerViewType) {
                        return columnCount;
                    } else {
                        return 1;
                    }
                }
            });
        }

        return layoutManager;
    }
}
