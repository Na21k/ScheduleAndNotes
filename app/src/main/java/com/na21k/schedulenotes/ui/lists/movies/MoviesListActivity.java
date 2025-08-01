package com.na21k.schedulenotes.ui.lists.movies;

import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.ScheduleNotesApplication;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.databinding.ActivityMoviesListBinding;
import com.na21k.schedulenotes.databinding.MovieInfoAlertViewBinding;
import com.na21k.schedulenotes.helpers.UiHelper;

import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

public class MoviesListActivity extends AppCompatActivity
        implements MoviesListAdapter.OnMovieActionRequestedListener {

    private static final int mLandscapeColumnCount = 2;
    private static final int mPortraitColumnCountTablet = 2;
    private static final int mLandscapeColumnCountTablet = 3;
    private final Observer<List<MoviesListItem>> mItemsObserver = new Observer<List<MoviesListItem>>() {
        @Override
        public void onChanged(List<MoviesListItem> moviesListItems) {
            moviesListItems.sort(Comparator.comparing(MoviesListItem::getText));
            mListAdapter.setMovies(moviesListItems);
        }
    };
    @Inject
    protected MoviesListViewModel.Factory mViewModelFactory;
    private MoviesListViewModel mViewModel;
    private ActivityMoviesListBinding mBinding;
    private MoviesListAdapter mListAdapter;
    private LiveData<List<MoviesListItem>> mLastSearchLiveData;
    private boolean isSearchMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((ScheduleNotesApplication) getApplicationContext())
                .getAppComponent()
                .inject(this);
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, mViewModelFactory).get(MoviesListViewModel.class);
        mBinding = ActivityMoviesListBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar.appBar);

        handleWindowInsets();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setUpList();
        setListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.simple_list_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                isSearchMode = true;
                mBinding.addMovieFab.hide();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                isSearchMode = false;
                mBinding.addMovieFab.show();
                return true;
            }
        });

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                replaceItemsObserverAccordingToSearchQuery(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void handleWindowInsets() {
        UiHelper.handleWindowInsets(getWindow(), mBinding.getRoot(),
                mBinding.container, mBinding.includedList.simpleList, mBinding.addMovieFab,
                true);
    }

    private void setUpList() {
        RecyclerView recyclerView = mBinding.includedList.simpleList;
        mListAdapter = new MoviesListAdapter(this);
        recyclerView.setAdapter(mListAdapter);
        LinearLayoutManager layoutManager = UiHelper.getRecyclerViewLayoutManager(this,
                mLandscapeColumnCountTablet, mPortraitColumnCountTablet, mLandscapeColumnCount);
        recyclerView.setLayoutManager(layoutManager);
        observeMovies();
    }

    private void observeMovies() {
        mViewModel.getAll().observe(this, mItemsObserver);
    }

    private void replaceItemsObserverAccordingToSearchQuery(String query) {
        mViewModel.getAll().removeObservers(this);

        if (mLastSearchLiveData != null) {
            mLastSearchLiveData.removeObservers(this);
        }

        mLastSearchLiveData = mViewModel.getItemsSearch(query);
        mLastSearchLiveData.observe(this, mItemsObserver);
    }

    private void setListeners() {
        mBinding.addMovieFab.setOnClickListener(v -> newMovie());

        mBinding.includedList.simpleList.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (isSearchMode) {
                        return;
                    }

                    if (scrollY <= oldScrollY) {
                        mBinding.addMovieFab.extend();
                    } else {
                        mBinding.addMovieFab.shrink();
                    }

                    if (!v.canScrollVertically(1) && v.canScrollVertically(-1)) {
                        mBinding.addMovieFab.hide();
                    } else {
                        mBinding.addMovieFab.show();
                    }
                });
    }

    private void newMovie() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_add_24);
        builder.setTitle(R.string.list_item_creation_alert_title);

        MovieInfoAlertViewBinding viewBinding = MovieInfoAlertViewBinding
                .inflate(getLayoutInflater(), mBinding.getRoot(), false);
        viewBinding.input.requestFocus();
        builder.setView(viewBinding.getRoot());

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            Editable movieTextEditable = viewBinding.input.getText();

            if (movieTextEditable != null && !movieTextEditable.toString().isEmpty()) {
                String movieText = movieTextEditable.toString();
                mViewModel.addNew(new MoviesListItem(0, movieText));
            } else {
                newMovie();
                UiHelper.showErrorDialog(this,
                        R.string.list_item_creation_empty_input_alert_message);
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });

        AlertDialog alertDialog = builder.show();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onMovieUpdateRequested(MoviesListItem movie) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_edit_24);
        builder.setTitle(R.string.list_item_editing_alert_title);

        MovieInfoAlertViewBinding viewBinding = MovieInfoAlertViewBinding
                .inflate(getLayoutInflater(), mBinding.getRoot(), false);
        viewBinding.input.setText(movie.getText());
        viewBinding.input.requestFocus();
        builder.setView(viewBinding.getRoot());

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            Editable movieTextEditable = viewBinding.input.getText();

            if (movieTextEditable != null && !movieTextEditable.toString().isEmpty()) {
                String movieText = movieTextEditable.toString();
                movie.setText(movieText);
                mViewModel.update(movie);
            } else {
                onMovieUpdateRequested(movie);
                UiHelper.showErrorDialog(this,
                        R.string.list_item_editing_empty_input_alert_message);
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });
        builder.setNeutralButton(R.string.delete,
                (dialog, which) -> onMovieDeletionRequested(movie));

        AlertDialog alertDialog = builder.show();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onMovieDeletionRequested(MoviesListItem movie) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_24);
        builder.setTitle(R.string.list_item_deletion_alert_title);
        builder.setMessage(R.string.list_item_deletion_alert_message);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            mViewModel.delete(movie);
            Snackbar.make(mBinding.getRoot(),
                            R.string.list_item_deleted_snackbar,
                            Constants.UNDO_DELETE_TIMEOUT_MILLIS)
                    .setAction(R.string.undo, v -> mViewModel.addNew(movie))
                    .show();
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }
}
