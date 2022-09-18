package com.na21k.schedulenotes.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.R;

public class UiHelper {

    public static boolean isInDarkMode(@NonNull Fragment fragment) {
        return isInDarkMode(fragment.getResources());
    }

    public static boolean isInDarkMode(@NonNull AppCompatActivity activity) {
        return isInDarkMode(activity.getResources());
    }

    public static boolean isInDarkMode(@NonNull Resources resources) {
        return (resources.getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
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

    public static void showSnackbar(@NonNull Context context,
                                    @NonNull View view,
                                    @StringRes int stringResourceId,
                                    int bottomInset) {
        makeSnackbar(context, view, stringResourceId, bottomInset, 3000).show();
    }

    public static void showSnackbar(@NonNull Context context,
                                    @NonNull View view,
                                    @NonNull String message,
                                    int bottomInset) {
        Snackbar snackbar = Snackbar
                .make(view, message, 3000);

        CoordinatorLayout.LayoutParams params = UiHelper
                .generateNewSnackbarLayoutParams(context, snackbar, bottomInset);
        snackbar.getView().setLayoutParams(params);

        snackbar.show();
    }

    @NonNull
    public static Snackbar makeSnackbar(@NonNull Context context,
                                        @NonNull View view,
                                        @StringRes int stringResourceId,
                                        int bottomInset,
                                        int durationMillis) {
        Snackbar snackbar = Snackbar
                .make(view, stringResourceId, durationMillis);

        CoordinatorLayout.LayoutParams params = UiHelper
                .generateNewSnackbarLayoutParams(context, snackbar, bottomInset);
        snackbar.getView().setLayoutParams(params);

        return snackbar;
    }

    @NonNull
    public static CoordinatorLayout.LayoutParams generateNewFabLayoutParams(
            @NonNull Context context,
            @NonNull ExtendedFloatingActionButton fab,
            int bottomInset,
            int gravity) {
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams());
        params.gravity = gravity;

        int pageMargin = (int) context.getResources().getDimension(R.dimen.page_margin);

        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin,
                pageMargin + bottomInset);

        return params;
    }

    @NonNull
    public static CoordinatorLayout.LayoutParams generateNewSnackbarLayoutParams(
            @NonNull Context context,
            @NonNull Snackbar snackbar,
            int bottomInset) {
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout
                .LayoutParams(snackbar.getView().getLayoutParams());

        int margin = (int) (context.getResources().getDimension(R.dimen.page_margin) / 2);
        params.setMargins(margin, margin, margin, bottomInset + margin);
        params.gravity = Gravity.BOTTOM;

        return params;
    }
}
