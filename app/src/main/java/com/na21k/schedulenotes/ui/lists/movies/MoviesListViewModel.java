package com.na21k.schedulenotes.ui.lists.movies;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class MoviesListViewModel extends ViewModel {

    @NonNull
    private final MutableRepository<MoviesListItem> mMutableMoviesListRepository;
    @NonNull
    private final CanSearchRepository<MoviesListItem> mCanSearchMoviesListRepository;

    private MoviesListViewModel(
            @NonNull MutableRepository<MoviesListItem> mutableMoviesListRepository,
            @NonNull CanSearchRepository<MoviesListItem> canSearchMoviesListRepository
    ) {
        super();

        mMutableMoviesListRepository = mutableMoviesListRepository;
        mCanSearchMoviesListRepository = canSearchMoviesListRepository;
    }

    public LiveData<List<MoviesListItem>> getAll() {
        return mMutableMoviesListRepository.getAll();
    }

    public LiveData<List<MoviesListItem>> getItemsSearch(String stringQuery) {
        return mCanSearchMoviesListRepository.getSearch(stringQuery);
    }

    public void addNew(MoviesListItem moviesListItem) {
        mMutableMoviesListRepository.add(moviesListItem);
    }

    public void update(MoviesListItem moviesListItem) {
        mMutableMoviesListRepository.update(moviesListItem);
    }

    public void delete(MoviesListItem moviesListItem) {
        mMutableMoviesListRepository.delete(moviesListItem);
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final MutableRepository<MoviesListItem> mMutableMoviesListRepository;
        @NonNull
        private final CanSearchRepository<MoviesListItem> mCanSearchMoviesListRepository;

        @Inject
        public Factory(
                @NonNull MutableRepository<MoviesListItem> mutableMoviesListRepository,
                @NonNull CanSearchRepository<MoviesListItem> canSearchMoviesListRepository
        ) {
            mMutableMoviesListRepository = mutableMoviesListRepository;
            mCanSearchMoviesListRepository = canSearchMoviesListRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            MoviesListViewModel vm = new MoviesListViewModel(
                    mMutableMoviesListRepository, mCanSearchMoviesListRepository
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
