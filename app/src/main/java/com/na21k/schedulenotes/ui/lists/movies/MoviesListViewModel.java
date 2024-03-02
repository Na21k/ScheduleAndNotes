package com.na21k.schedulenotes.ui.lists.movies;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.repositories.lists.MoviesListRepository;

import java.util.List;

public class MoviesListViewModel extends AndroidViewModel {

    private final MoviesListRepository mMoviesListRepository;

    public MoviesListViewModel(@NonNull Application application) {
        super(application);

        mMoviesListRepository = new MoviesListRepository(application);
    }

    public LiveData<List<MoviesListItem>> getAll() {
        return mMoviesListRepository.getAll();
    }

    public LiveData<List<MoviesListItem>> getItemsSearch(String stringQuery) {
        return mMoviesListRepository.getSearch(stringQuery);
    }

    public void addNew(MoviesListItem moviesListItem) {
        mMoviesListRepository.add(moviesListItem);
    }

    public void update(MoviesListItem moviesListItem) {
        mMoviesListRepository.update(moviesListItem);
    }

    public void delete(MoviesListItem moviesListItem) {
        mMoviesListRepository.delete(moviesListItem);
    }
}
