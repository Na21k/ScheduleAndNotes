package com.na21k.schedulenotes.ui.lists.music;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.repositories.lists.MusicListRepository;

import java.util.List;

public class MusicListViewModel extends AndroidViewModel {

    private final MusicListRepository mMusicListRepository;

    public MusicListViewModel(@NonNull Application application) {
        super(application);

        mMusicListRepository = new MusicListRepository(application);
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
}
