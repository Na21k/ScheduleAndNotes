package com.na21k.schedulenotes.ui.lists.movies;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.repositories.lists.MoviesListRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class MoviesListViewModel extends ViewModel {

    @NonNull
    private final MoviesListRepository mMoviesListRepository;

    private MoviesListViewModel(@NonNull MoviesListRepository moviesListRepository) {
        super();

        mMoviesListRepository = moviesListRepository;
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

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final MoviesListRepository mMoviesListRepository;

        @Inject
        public Factory(@NonNull MoviesListRepository moviesListRepository) {
            mMoviesListRepository = moviesListRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            MoviesListViewModel vm = new MoviesListViewModel(mMoviesListRepository);
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
