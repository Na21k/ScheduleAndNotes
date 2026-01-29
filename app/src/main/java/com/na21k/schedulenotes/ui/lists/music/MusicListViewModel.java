package com.na21k.schedulenotes.ui.lists.music;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class MusicListViewModel extends ViewModel {

    @NonNull
    private final MutableRepository<MusicListItem> mMutableMusicListRepository;
    @NonNull
    private final CanSearchRepository<MusicListItem> mCanSearchMusicListRepository;

    private MusicListViewModel(
            @NonNull MutableRepository<MusicListItem> mutableMusicListRepository,
            @NonNull CanSearchRepository<MusicListItem> canSearchMusicListRepository
    ) {
        super();

        mMutableMusicListRepository = mutableMusicListRepository;
        mCanSearchMusicListRepository = canSearchMusicListRepository;
    }

    public LiveData<List<MusicListItem>> getAll() {
        return mMutableMusicListRepository.getAll();
    }

    public LiveData<List<MusicListItem>> getItemsSearch(String searchQuery) {
        return mCanSearchMusicListRepository.getSearch(searchQuery);
    }

    public void addNew(MusicListItem musicListItem) {
        mMutableMusicListRepository.add(musicListItem);
    }

    public void update(MusicListItem musicListItem) {
        mMutableMusicListRepository.update(musicListItem);
    }

    public void delete(MusicListItem musicListItem) {
        mMutableMusicListRepository.delete(musicListItem);
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final MutableRepository<MusicListItem> mMutableMusicListRepository;
        @NonNull
        private final CanSearchRepository<MusicListItem> mCanSearchMusicListRepository;

        @Inject
        public Factory(
                @NonNull MutableRepository<MusicListItem> mutableMusicListRepository,
                @NonNull CanSearchRepository<MusicListItem> canSearchMusicListRepository
        ) {
            mMutableMusicListRepository = mutableMusicListRepository;
            mCanSearchMusicListRepository = canSearchMusicListRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            MusicListViewModel vm = new MusicListViewModel(
                    mMutableMusicListRepository, mCanSearchMusicListRepository
            );
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
