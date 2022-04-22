package com.na21k.schedulenotes.ui.lists.languages;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemDao;

import java.util.List;

public class LanguagesListViewModel extends AndroidViewModel {

    private final LanguagesListItemDao mLanguagesListItemDao;
    private final LiveData<List<LanguagesListItem>> mAllItems;

    public LanguagesListViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mLanguagesListItemDao = db.languagesListItemDao();
        mAllItems = mLanguagesListItemDao.getAll();
    }

    public LiveData<List<LanguagesListItem>> getAll() {
        return mAllItems;
    }

    public void addNew(LanguagesListItem item) {
        new Thread(() -> mLanguagesListItemDao.insert(item)).start();
    }

    public void update(LanguagesListItem item) {
        new Thread(() -> mLanguagesListItemDao.update(item)).start();
    }

    public void delete(LanguagesListItem item) {
        new Thread(() -> mLanguagesListItemDao.delete(item)).start();
    }
}
