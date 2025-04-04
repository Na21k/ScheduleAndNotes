package com.na21k.schedulenotes.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;

public class UiHelper {

    public static boolean isInDarkMode(@NonNull Fragment fragment) {
        return isInDarkMode(fragment.getResources());
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

    public static void showSnackbar(@NonNull View view, @StringRes int stringResourceId) {
        Snackbar.make(view, stringResourceId, Constants.DEFAULT_SNACKBAR_TIMEOUT_MILLIS).show();
    }

    public static void showSnackbar(@NonNull View view, @NonNull String message) {
        Snackbar.make(view, message, Constants.DEFAULT_SNACKBAR_TIMEOUT_MILLIS).show();
    }

    /**
     * Makes sure respective insets are set as respective paddings/margins for the views passed.</br>
     * This takes system bars, display cutout, amd IME into account.
     * All other paddings for <tt>containerView</tt> and <tt>bottomInsetView</tt> will be set to 0.</br>
     * If a floating action button is passed,
     * its {@link CoordinatorLayout.LayoutParams} will be replaced.
     *
     * @param rootView        the view to set {@link androidx.core.view.OnApplyWindowInsetsListener} on
     * @param containerView   the view to set left, top, and right insets as respective paddings for
     * @param bottomInsetView the view to set bottom inset as bottom padding for
     * @param fab             the floating action button to set bottom inset + page_margin
     *                        (from resources) as bottom margin for
     * @param consume         whether the {@link androidx.core.view.OnApplyWindowInsetsListener}
     *                        should consume the insets
     * @implNote <tt>containerView</tt> and <tt>bottomInsetView</tt> may be the same object.
     */
    public static void handleWindowInsets(@NonNull Window window, @NonNull View rootView,
                                          @Nullable View containerView,
                                          @Nullable View bottomInsetView,
                                          @Nullable ExtendedFloatingActionButton fab,
                                          boolean consume) {
        boolean isInDarkMode = UiHelper.isInDarkMode(rootView.getResources());
        //breaks the status bar color on Android 12, 13, 14 for some reason
        //and makes it white in light theme
        WindowCompat.getInsetsController(window, rootView)
                .setAppearanceLightNavigationBars(!isInDarkMode);
        //fixes the status bar color
        WindowCompat.getInsetsController(window, rootView)
                .setAppearanceLightStatusBars(!isInDarkMode);

        WindowCompat.setDecorFitsSystemWindows(window, false);
        window.setNavigationBarColor(Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            final Insets i = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
                            | WindowInsetsCompat.Type.ime());

            if (containerView == bottomInsetView && containerView != null) {
                containerView.setPadding(i.left, i.top, i.right, i.bottom);
            } else {
                if (containerView != null) {
                    containerView.setPadding(i.left, i.top, i.right, 0);
                }
                if (bottomInsetView != null) {
                    bottomInsetView.setPadding(0, 0, 0, i.bottom);
                }
            }

            if (fab != null) {
                CoordinatorLayout.LayoutParams newFabParams = generateNewFabLayoutParams(
                        rootView.getContext(),
                        fab,
                        i.bottom,
                        Gravity.END | Gravity.BOTTOM);

                fab.setLayoutParams(newFabParams);
            }

            return consume ? WindowInsetsCompat.CONSUMED : insets;
        });
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
}
