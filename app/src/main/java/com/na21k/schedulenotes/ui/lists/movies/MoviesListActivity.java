package com.na21k.schedulenotes.ui.lists.movies;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.databinding.ActivityMoviesListBinding;
import com.na21k.schedulenotes.databinding.MovieInfoAlertViewBinding;
import com.na21k.schedulenotes.helpers.UiHelper;

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
        setListeners();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setUpList() {
        RecyclerView recyclerView = mBinding.includedList.simpleList;
        MoviesListAdapter adapter = new MoviesListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        observeMovies(adapter);
    }

    private void observeMovies(MoviesListAdapter adapter) {
        mViewModel.getAll().observe(this, moviesListItems -> {
            moviesListItems.sort(Comparator.comparing(MoviesListItem::getText));
            adapter.setMovies(moviesListItems);
        });
    }

    private void setListeners() {
        mBinding.addMovieFab.setOnClickListener(v -> newMovie());

        mBinding.includedList.simpleList.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
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

        builder.show();
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
        builder.setNeutralButton(R.string.cancel, (dialog, which) -> {
        });
        builder.setNegativeButton(R.string.delete,
                (dialog, which) -> onMovieDeletionRequested(movie));

        builder.show();
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
