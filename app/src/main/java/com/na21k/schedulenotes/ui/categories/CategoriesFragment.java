package com.na21k.schedulenotes.ui.categories;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.UiHelper;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.databinding.CategoriesFragmentBinding;
import com.na21k.schedulenotes.ui.categories.categoryDetails.CategoryDetailsActivity;

import java.util.Comparator;

public class CategoriesFragment extends Fragment {

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

        //mViewModel.deleteAll();

        /*mViewModel.insert(new Category(0, "Test category 1", 0xff5be02b));
        mViewModel.insert(new Category(0, "Test category 2", 0xffe0792b));
        mViewModel.insert(new Category(0, "Test category 3", 0xffe02b2b));
        mViewModel.insert(new Category(0, "Test category 4", 0xffd42aac));
        mViewModel.insert(new Category(0, "Test category 5", 0xff2ad4c3));
        mViewModel.insert(new Category(0, "Test category 6", 0xffd4bd2a));
        mViewModel.insert(new Category(0, "Test category 7", 0xff2a66d4));
        mViewModel.insert(new Category(0, "Test category 8", 0xff5be02b));
        mViewModel.insert(new Category(0, "Test category 9", 0xffe0792b));
        mViewModel.insert(new Category(0, "Test category 10", 0xffe02b2b));
        mViewModel.insert(new Category(0, "Test category 11", 0xffd42aac));
        mViewModel.insert(new Category(0, "Test category 12", 0xff2ad4c3));
        mViewModel.insert(new Category(0, "Test category 13", 0xffd4bd2a));
        mViewModel.insert(new Category(0, "Test category 14", 0xff2a66d4));*/

        observeCategories(adapter);
        setListeners();
    }

    private CategoriesListAdapter setUpRecyclerView() {
        RecyclerView recyclerView = mBinding.includedList.categoriesList;
        CategoriesListAdapter adapter =
                new CategoriesListAdapter(mViewModel, UiHelper.isInDarkMode(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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

                    if (v.canScrollVertically(1)) {
                        mBinding.addCategoryFab.show();
                    } else {
                        mBinding.addCategoryFab.hide();
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
}
