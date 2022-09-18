package com.na21k.schedulenotes.ui.lists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.data.models.UserDefinedListModel;
import com.na21k.schedulenotes.databinding.ListsFragmentBinding;
import com.na21k.schedulenotes.databinding.UserDefinedListInfoAlertViewBinding;
import com.na21k.schedulenotes.helpers.UiHelper;
import com.na21k.schedulenotes.ui.lists.languages.LanguagesListActivity;
import com.na21k.schedulenotes.ui.lists.movies.MoviesListActivity;
import com.na21k.schedulenotes.ui.lists.music.MusicListActivity;
import com.na21k.schedulenotes.ui.lists.shopping.ShoppingListActivity;
import com.na21k.schedulenotes.ui.lists.userDefinedLists.UserDefinedListActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListsFragment extends Fragment
        implements ListsListAdapter.OnListActionRequestedListener, MenuProvider {

    private final Observer<List<UserDefinedList>> mListsObserver = new Observer<List<UserDefinedList>>() {
        @Override
        public void onChanged(List<UserDefinedList> userDefinedLists) {
            mViewModel.setListsCache(userDefinedLists);
            updateListIfEnoughData();
        }
    };
    private ListsViewModel mViewModel;
    private ListsFragmentBinding mBinding;
    private ListsListAdapter mListAdapter;
    private LiveData<List<UserDefinedList>> mLastSearchLiveData;
    private boolean isSearchMode = false;

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
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.CREATED);

        mListAdapter = setUpRecyclerView();
        setObservers();
        setListeners();
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.lists_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                isSearchMode = true;
                mBinding.addListFab.hide();
                mBinding.defaultListsArea.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                isSearchMode = false;
                mBinding.addListFab.show();
                mBinding.defaultListsArea.setVisibility(View.VISIBLE);
                return true;
            }
        });

        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                replaceListsObserverAccordingToSearchQuery(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    private ListsListAdapter setUpRecyclerView() {
        RecyclerView recyclerView = mBinding.includedList.listsList;
        ListsListAdapter adapter = new ListsListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return adapter;
    }

    private void setObservers() {
        mViewModel.getAllLists().observe(getViewLifecycleOwner(), mListsObserver);
        mViewModel.getAllListItems().observe(getViewLifecycleOwner(), userDefinedListItems -> {
            mViewModel.setListItemsCache(userDefinedListItems);
            updateListIfEnoughData();
        });
    }

    private void replaceListsObserverAccordingToSearchQuery(String query) {
        mViewModel.getAllLists().removeObservers(getViewLifecycleOwner());

        if (mLastSearchLiveData != null) {
            mLastSearchLiveData.removeObservers(getViewLifecycleOwner());
        }

        mLastSearchLiveData = mViewModel.getListsSearch(query);
        mLastSearchLiveData.observe(getViewLifecycleOwner(), mListsObserver);
    }

    private void updateListIfEnoughData() {
        List<UserDefinedList> listsCache = mViewModel.getListsCache();
        List<UserDefinedListItem> listItemsCache = mViewModel.getListItemsCache();

        boolean isEnoughData = listsCache != null && listItemsCache != null;

        if (!isEnoughData) {
            return;
        }

        List<UserDefinedListModel> models = new ArrayList<>();

        for (UserDefinedList list : listsCache) {
            int listId = list.getId();
            int itemsCount = listItemsCache.stream()
                    .filter(listItem -> listItem.getListId() == listId)
                    .mapToInt(value -> 1).sum();

            models.add(new UserDefinedListModel(list, itemsCount));
        }

        models.sort(Comparator.comparing(UserDefinedListModel::getTitle));
        mListAdapter.setLists(models);
    }

    private void setListeners() {
        mBinding.addListFab.setOnClickListener(v -> newList());
        mBinding.moviesListBtnCard.setOnClickListener(v -> openMoviesList());
        mBinding.musicListBtnCard.setOnClickListener(v -> openMusicList());
        mBinding.shoppingListBtnCard.setOnClickListener(v -> openShoppingList());
        mBinding.languagesListBtnCard.setOnClickListener(v -> openLanguagesList());

        mBinding.includedList.listsList.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (isSearchMode) {
                        return;
                    }

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
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.ic_add_24);
            builder.setTitle(R.string.list_creation_alert_title);

            UserDefinedListInfoAlertViewBinding viewBinding = UserDefinedListInfoAlertViewBinding
                    .inflate(getLayoutInflater(), mBinding.getRoot(), false);
            viewBinding.input.requestFocus();
            builder.setView(viewBinding.getRoot());

            builder.setPositiveButton(R.string.save, (dialog, which) -> {
                Editable listNameEditable = viewBinding.input.getText();

                if (listNameEditable != null && !listNameEditable.toString().isEmpty()) {
                    String listName = listNameEditable.toString();
                    mViewModel.addNew(new UserDefinedList(0, listName));
                } else {
                    newList();
                    UiHelper.showErrorDialog(context,
                            R.string.list_creation_empty_input_alert_message);
                }
            });
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            });

            builder.show();
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

    private void openShoppingList() {
        Context context = getContext();

        if (context != null) {
            Intent intent = new Intent(context, ShoppingListActivity.class);
            startActivity(intent);
        }
    }

    private void openLanguagesList() {
        Context context = getContext();

        if (context != null) {
            Intent intent = new Intent(context, LanguagesListActivity.class);
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
            bundle.putString(Constants.LIST_TITLE_INTENT_KEY, list.getTitle());
            intent.putExtras(bundle);

            context.startActivity(intent);
        }
    }

    @Override
    public void onListRenameRequested(UserDefinedList list) {
        Context context = getContext();

        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.ic_edit_24);
            builder.setTitle(R.string.list_rename_alert_title);

            UserDefinedListInfoAlertViewBinding viewBinding = UserDefinedListInfoAlertViewBinding
                    .inflate(getLayoutInflater(), mBinding.getRoot(), false);
            viewBinding.input.setText(list.getTitle());
            viewBinding.input.requestFocus();
            builder.setView(viewBinding.getRoot());

            builder.setPositiveButton(R.string.save, (dialog, which) -> {
                Editable listNameEditable = viewBinding.input.getText();

                if (listNameEditable != null && !listNameEditable.toString().isEmpty()) {
                    String listName = listNameEditable.toString();
                    String titleOld = list.getTitle();
                    list.setTitle(listName);

                    mViewModel.update(list, (t, e) -> {
                        if (e.getClass().equals(SQLiteConstraintException.class)) {
                            Activity activity = getActivity();

                            if (activity != null) {
                                activity.runOnUiThread(() -> {
                                    list.setTitle(titleOld);

                                    onListRenameRequested(list);
                                    UiHelper.showErrorDialog(context,
                                            R.string.such_list_already_exists_alert_message);
                                });
                            }
                        } else {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    onListRenameRequested(list);
                    UiHelper.showErrorDialog(context,
                            R.string.list_renaming_empty_input_alert_message);
                }
            });
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            });

            builder.show();
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
