package com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemDao;

public class WordOrPhraseDetailsViewModel extends AndroidViewModel {

    private final LanguagesListItemDao mLanguagesListItemDao;
    private LiveData<LanguagesListItem> mItem;
    private int mItemId;

    public WordOrPhraseDetailsViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mLanguagesListItemDao = db.languagesListItemDao();
    }

    public LiveData<LanguagesListItem> getById(int id) {
        if (mItemId != id) {
            mItem = mLanguagesListItemDao.getById(id);
            mItemId = id;
        }

        return mItem;
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
