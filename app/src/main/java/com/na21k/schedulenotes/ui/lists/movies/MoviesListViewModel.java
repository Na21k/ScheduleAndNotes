package com.na21k.schedulenotes.ui.lists.movies;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItemDao;

import java.util.List;

public class MoviesListViewModel extends AndroidViewModel {

    private final MoviesListItemDao mMoviesListItemDao;
    private final LiveData<List<MoviesListItem>> mAllMovies;

    public MoviesListViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mMoviesListItemDao = db.moviesListItemDao();
        mAllMovies = mMoviesListItemDao.getAll();
    }

    public LiveData<List<MoviesListItem>> getAll() {
        return mAllMovies;
    }

    public void addNew(MoviesListItem moviesListItem) {
        new Thread(() -> mMoviesListItemDao.insert(moviesListItem)).start();
    }

    public void update(MoviesListItem moviesListItem) {
        new Thread(() -> mMoviesListItemDao.update(moviesListItem)).start();
    }

    public void delete(MoviesListItem moviesListItem) {
        new Thread(() -> mMoviesListItemDao.delete(moviesListItem)).start();
    }
}
