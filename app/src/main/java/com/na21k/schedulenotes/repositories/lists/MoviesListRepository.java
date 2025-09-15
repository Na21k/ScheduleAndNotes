package com.na21k.schedulenotes.repositories.lists;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItemDao;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanProvideRandomRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

import javax.inject.Inject;

public class MoviesListRepository extends MutableRepository<MoviesListItem>
        implements CanSearchRepository<MoviesListItem>, CanClearRepository<MoviesListItem>,
        CanProvideRandomRepository<MoviesListItem> {

    private final MoviesListItemDao mMoviesListItemDao = db.moviesListItemDao();

    @Inject
    public MoviesListRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    protected BaseDao<MoviesListItem> getDao() {
        return mMoviesListItemDao;
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
    public MoviesListItem getRandomBlocking() {
        return mMoviesListItemDao.getRandomBlocking();
    }
}
