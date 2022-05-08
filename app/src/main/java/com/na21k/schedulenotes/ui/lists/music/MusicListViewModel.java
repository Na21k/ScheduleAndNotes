package com.na21k.schedulenotes.ui.lists.music;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItemDao;

import java.util.List;

public class MusicListViewModel extends AndroidViewModel {

    private final MusicListItemDao mMusicListItemDao;
    private final LiveData<List<MusicListItem>> mAllMusic;

    public MusicListViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mMusicListItemDao = db.musicListItemDao();
        mAllMusic = mMusicListItemDao.getAll();
    }

    public LiveData<List<MusicListItem>> getAll() {
        return mAllMusic;
    }

    public LiveData<List<MusicListItem>> getItemsSearch(String searchQuery) {
        return mMusicListItemDao.search(searchQuery);
    }

    public void addNew(MusicListItem musicListItem) {
        new Thread(() -> mMusicListItemDao.insert(musicListItem)).start();
    }

    public void update(MusicListItem musicListItem) {
        new Thread(() -> mMusicListItemDao.update(musicListItem)).start();
    }

    public void delete(MusicListItem musicListItem) {
        new Thread(() -> mMusicListItemDao.delete(musicListItem)).start();
    }
}
