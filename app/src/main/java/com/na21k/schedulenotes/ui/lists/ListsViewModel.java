package com.na21k.schedulenotes.ui.lists;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListDao;

import java.util.List;

public class ListsViewModel extends AndroidViewModel {

    private final UserDefinedListDao mListDao;
    private final LiveData<List<UserDefinedList>> mAllLists;

    public ListsViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mListDao = db.userDefinedListDao();
        mAllLists = mListDao.getAll();
    }

    public LiveData<List<UserDefinedList>> getAll() {
        return mAllLists;
    }

    public int getListItemsCount(int listId) {
        return mListDao.getListItemsCount(listId);
    }

    public void addNew(UserDefinedList list) {
        new Thread(() -> mListDao.insert(list)).start();
    }

    public void update(UserDefinedList list) {
        new Thread(() -> mListDao.update(list)).start();
    }

    public void delete(UserDefinedList list) {
        new Thread(() -> mListDao.delete(list)).start();
    }
}
