package com.na21k.schedulenotes.di.modules.repositories.lists;

import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanProvideRandomRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.Repository;
import com.na21k.schedulenotes.repositories.lists.MusicListRepository;

import dagger.Binds;
import dagger.Module;

@Module
public interface MusicListRepositoriesModule {

    @Binds
    Repository<MusicListItem> bindMusicListRepository(MusicListRepository musicListRepository);

    @Binds
    MutableRepository<MusicListItem> bindMusicListRepositoryMutable(MusicListRepository musicListRepository);

    @Binds
    CanSearchRepository<MusicListItem> bindCanSearchMusicListRepository(MusicListRepository musicListRepository);

    @Binds
    CanClearRepository<MusicListItem> bindCanClearMusicListRepository(MusicListRepository musicListRepository);

    @Binds
    CanProvideRandomRepository<MusicListItem> bindCanProvideRandomMusicListRepository(MusicListRepository musicListRepository);
}
