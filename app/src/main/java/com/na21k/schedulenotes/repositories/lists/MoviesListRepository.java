package com.na21k.schedulenotes.repositories.lists;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItemDao;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

public class MoviesListRepository extends MutableRepository<MoviesListItem>
        implements CanSearchRepository<MoviesListItem>, CanClearRepository {

    private final MoviesListItemDao mMoviesListItemDao = db.moviesListItemDao();

    public MoviesListRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<List<MoviesListItem>> getSearch(String query) {
        return mMoviesListItemDao.search(query);
    }

    @Override
    public void clearBlocking() {
        mMoviesListItemDao.deleteAll();
    }

    @Override
    protected BaseDao<MoviesListItem> getDao() {
        return mMoviesListItemDao;
    }
}
