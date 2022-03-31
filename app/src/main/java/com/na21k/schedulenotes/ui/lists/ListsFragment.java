package com.na21k.schedulenotes.ui.lists;

import android.app.Activity;
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
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.models.UserDefinedListModel;
import com.na21k.schedulenotes.databinding.ListsFragmentBinding;
import com.na21k.schedulenotes.ui.lists.movies.MoviesListActivity;
import com.na21k.schedulenotes.ui.lists.music.MusicListActivity;
import com.na21k.schedulenotes.ui.lists.userDefinedLists.UserDefinedListActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        mViewModel.getAll().observe(getViewLifecycleOwner(), userDefinedLists -> new Thread(() -> {
            List<UserDefinedListModel> models = new ArrayList<>();

            for (UserDefinedList list : userDefinedLists) {
                int listId = list.getId();
                int itemsCount = mViewModel.getListItemsCount(listId);
                models.add(new UserDefinedListModel(list, itemsCount));
            }

            models.sort(Comparator.comparing(UserDefinedListModel::getTitle));

            Activity activity = getActivity();

            if (activity != null) {
                activity.runOnUiThread(() -> adapter.setLists(models));
            }
        }).start());
    }

    private void setListeners() {
        mBinding.addListFab.setOnClickListener(v -> newList());
        mBinding.moviesListBtnCard.setOnClickListener(v -> openMoviesList());
        mBinding.musicListBtnCard.setOnClickListener(v -> openMusicList());

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
            Intent intent = new Intent(context, UserDefinedListActivity.class);
            context.startActivity(intent);
        }
    }

    private void openMoviesList() {
        Context context = getContext();

        if (context != null) {
            Intent intent = new Intent(context, MoviesListActivity.class);
            startActivity(intent);
        }
    }

    private void openMusicList() {
        Context context = getContext();

        if (context != null) {
            Intent intent = new Intent(context, MusicListActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onListOpenRequested(UserDefinedList list) {
        Context context = getContext();

        if (context != null) {
            Intent intent = new Intent(context, UserDefinedListActivity.class);

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.LIST_ID_INTENT_KEY, list.getId());
            intent.putExtras(bundle);

            context.startActivity(intent);
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
