package com.na21k.schedulenotes.repositories.lists;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItem;
import com.na21k.schedulenotes.data.database.Lists.Music.MusicListItemDao;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

public class MusicListRepository extends MutableRepository<MusicListItem>
        implements CanSearchRepository<MusicListItem>, CanClearRepository {

    private final MusicListItemDao mMusicListItemDao = db.musicListItemDao();

    public MusicListRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<List<MusicListItem>> getSearch(String query) {
        return mMusicListItemDao.search(query);
    }

    @Override
    public void clearBlocking() {
        mMusicListItemDao.deleteAll();
    }

    @Override
    protected BaseDao<MusicListItem> getDao() {
        return mMusicListItemDao;
    }
}
