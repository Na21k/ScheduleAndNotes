package com.na21k.schedulenotes.ui.categories;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.databinding.CategoriesFragmentBinding;
import com.na21k.schedulenotes.helpers.UiHelper;
import com.na21k.schedulenotes.ui.categories.categoryDetails.CategoryDetailsActivity;

import java.util.Comparator;

public class CategoriesFragment extends Fragment
        implements CategoriesListAdapter.OnCategoryActionRequestedListener {

    private static final int mLandscapeColumnCount = 2;
    private static final int mPortraitColumnCountTablet = 2;
    private static final int mLandscapeColumnCountTablet = 3;
    private CategoriesViewModel mViewModel;
    private CategoriesFragmentBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = CategoriesFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CategoriesListAdapter adapter = setUpRecyclerView();
        observeCategories(adapter);
        setListeners();
    }

    private CategoriesListAdapter setUpRecyclerView() {
        RecyclerView recyclerView = mBinding.includedList.categoriesList;
        CategoriesListAdapter adapter =
                new CategoriesListAdapter(UiHelper.isInDarkMode(this), this);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = UiHelper.getRecyclerViewLayoutManager(requireContext(),
                mLandscapeColumnCountTablet, mPortraitColumnCountTablet, mLandscapeColumnCount);
        recyclerView.setLayoutManager(layoutManager);

        return adapter;
    }

    private void observeCategories(CategoriesListAdapter adapter) {
        mViewModel.getAll().observe(getViewLifecycleOwner(), categories -> {
            categories.sort(Comparator.comparing(Category::getTitle));
            adapter.setCategories(categories);
        });
    }

    private void setListeners() {
        mBinding.addCategoryFab.setOnClickListener(v -> newCategory());

        mBinding.includedList.categoriesList.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY <= oldScrollY) {
                        mBinding.addCategoryFab.extend();
                    } else {
                        mBinding.addCategoryFab.shrink();
                    }

                    if (!v.canScrollVertically(1) && v.canScrollVertically(-1)) {
                        mBinding.addCategoryFab.hide();
                    } else {
                        mBinding.addCategoryFab.show();
                    }
                });
    }

    private void newCategory() {
        Context context = getContext();

        if (context != null) {
            Intent intent = new Intent(context, CategoryDetailsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onCategoryOpenRequested(Category category) {
        Context context = getContext();

        if (context != null) {
            Intent intent = new Intent(context, CategoryDetailsActivity.class);

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.CATEGORY_ID_INTENT_KEY, category.getId());
            intent.putExtras(bundle);

            context.startActivity(intent);
        }
    }

    @Override
    public void onCategoryDeletionRequested(Category category) {
        Context context = getContext();

        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.ic_delete_24);
            builder.setTitle(R.string.category_deletion_alert_title);
            builder.setMessage(R.string.category_deletion_alert_message);

            builder.setPositiveButton(R.string.delete, (dialog, which) -> {
                mViewModel.delete(category);
                Snackbar.make(mBinding.getRoot(), R.string.category_deleted_snackbar, 3000).show();
            });
            builder.setNegativeButton(R.string.keep, (dialog, which) -> {
            });

            builder.show();
        }
    }
}
