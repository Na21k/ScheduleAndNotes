package com.na21k.schedulenotes.ui.lists.music;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.repositories.lists.MusicListRepository;
import com.na21k.schedulenotes.ui.shared.BaseViewModelFactory;

import java.util.List;

import javax.inject.Inject;

public class MusicListViewModel extends ViewModel {

    @NonNull
    private final MusicListRepository mMusicListRepository;

    private MusicListViewModel(@NonNull MusicListRepository musicListRepository) {
        super();

        mMusicListRepository = musicListRepository;
    }

    public LiveData<List<MusicListItem>> getAll() {
        return mMusicListRepository.getAll();
    }

    public LiveData<List<MusicListItem>> getItemsSearch(String searchQuery) {
        return mMusicListRepository.getSearch(searchQuery);
    }

    public void addNew(MusicListItem musicListItem) {
        mMusicListRepository.add(musicListItem);
    }

    public void update(MusicListItem musicListItem) {
        mMusicListRepository.update(musicListItem);
    }

    public void delete(MusicListItem musicListItem) {
        mMusicListRepository.delete(musicListItem);
    }

    public static class Factory extends BaseViewModelFactory {

        @NonNull
        private final MusicListRepository mMusicListRepository;

        @Inject
        public Factory(@NonNull MusicListRepository musicListRepository) {
            mMusicListRepository = musicListRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            MusicListViewModel vm = new MusicListViewModel(mMusicListRepository);
            ensureViewModelType(vm, modelClass);

            return (T) vm;
        }
    }
}
