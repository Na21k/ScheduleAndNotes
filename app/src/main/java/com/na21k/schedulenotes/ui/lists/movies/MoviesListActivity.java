package com.na21k.schedulenotes.ui.lists.movies;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.databinding.ActivityMoviesListBinding;

import java.util.Comparator;

public class MoviesListActivity extends AppCompatActivity
        implements MoviesListAdapter.OnMovieActionRequestedListener {

    private MoviesListViewModel mViewModel;
    private ActivityMoviesListBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(MoviesListViewModel.class);
        mBinding = ActivityMoviesListBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setUpList();
    }

    private void setUpList() {
        MoviesListAdapter adapter = new MoviesListAdapter(this);
        observeMovies(adapter);
        setListeners();
    }

    private void observeMovies(MoviesListAdapter adapter) {
        mViewModel.getAll().observe(this, moviesListItems -> {
            moviesListItems.sort(Comparator.comparing(MoviesListItem::getText));
            adapter.setMovies(moviesListItems);
        });
    }

    private void setListeners() {
        mBinding.addMovieFab.setOnClickListener(v -> newMovie());
    }

    private void newMovie() {

    }

    @Override
    public void onMovieDeletionRequested(MoviesListItem movie) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_24);
        builder.setTitle(R.string.list_item_deletion_alert_title);
        builder.setMessage(R.string.list_item_deletion_alert_message);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            mViewModel.delete(movie);
            Snackbar.make(mBinding.getRoot(), R.string.list_item_deleted_snackbar, 3000).show();
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }
}
