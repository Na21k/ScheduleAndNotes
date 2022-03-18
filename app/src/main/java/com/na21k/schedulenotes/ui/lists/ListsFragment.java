package com.na21k.schedulenotes.ui.lists;

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
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.databinding.ListsFragmentBinding;
import com.na21k.schedulenotes.ui.lists.movies.MoviesListActivity;

import java.util.Comparator;

public class ListsFragment extends Fragment
        implements ListsListAdapter.OnListActionRequestedListener {

    private ListsViewModel mViewModel;
    private ListsFragmentBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ListsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = ListsFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListsListAdapter adapter = setUpRecyclerView();
        observeLists(adapter);
        setListeners();
    }

    private ListsListAdapter setUpRecyclerView() {
        RecyclerView recyclerView = mBinding.includedList.listsList;
        ListsListAdapter adapter = new ListsListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return adapter;
    }

    private void observeLists(ListsListAdapter adapter) {
        mViewModel.getAll().observe(getViewLifecycleOwner(), userDefinedLists -> {
            userDefinedLists.sort(Comparator.comparing(UserDefinedList::getTitle));
            adapter.setLists(userDefinedLists);
        });
    }

    private void setListeners() {
        mBinding.addListFab.setOnClickListener(v -> newList());
        mBinding.moviesListBtnCard.setOnClickListener(v -> openMoviesList());

        mBinding.includedList.listsList.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY <= oldScrollY) {
                        mBinding.addListFab.extend();
                    } else {
                        mBinding.addListFab.shrink();
                    }

                    if (!v.canScrollVertically(1) && v.canScrollVertically(-1)) {
                        mBinding.addListFab.hide();
                    } else {
                        mBinding.addListFab.show();
                    }
                });
    }

    private void newList() {
        Context context = getContext();

        if (context != null) {
            Snackbar.make(mBinding.getRoot(), "Test", 3000).show();
        }
    }

    private void openMoviesList() {
        Context context = getContext();

        if (context != null) {
            Intent intent = new Intent(context, MoviesListActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onListDeletionRequested(UserDefinedList list) {
        Context context = getContext();

        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.ic_delete_24);
            builder.setTitle(R.string.list_deletion_alert_title);
            builder.setMessage(R.string.list_deletion_alert_message);

            builder.setPositiveButton(R.string.delete, (dialog, which) -> {
                mViewModel.delete(list);
                Snackbar.make(mBinding.getRoot(), R.string.list_deleted_snackbar, 3000).show();
            });
            builder.setNegativeButton(R.string.keep, (dialog, which) -> {
            });

            builder.show();
        }
    }
}
