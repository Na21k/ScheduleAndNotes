package com.na21k.schedulenotes.di.modules.repositories.lists;

import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanProvideRandomRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.Repository;
import com.na21k.schedulenotes.repositories.lists.MoviesListRepository;

import dagger.Binds;
import dagger.Module;

@Module
public interface MoviesListRepositoriesModule {

    @Binds
    Repository<MoviesListItem> bindMoviesListRepository(MoviesListRepository moviesListRepository);

    @Binds
    MutableRepository<MoviesListItem> bindMoviesListRepositoryMutable(MoviesListRepository moviesListRepository);

    @Binds
    CanSearchRepository<MoviesListItem> bindCanSearchMoviesListRepository(MoviesListRepository moviesListRepository);

    @Binds
    CanClearRepository<MoviesListItem> bindCanClearMoviesListRepository(MoviesListRepository moviesListRepository);

    @Binds
    CanProvideRandomRepository<MoviesListItem> bindCanProvideRandomMoviesListRepository(MoviesListRepository moviesListRepository);
}
