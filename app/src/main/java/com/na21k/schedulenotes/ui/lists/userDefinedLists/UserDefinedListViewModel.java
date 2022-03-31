package com.na21k.schedulenotes.ui.lists.userDefinedLists;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.AppDatabase;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItemDao;

import java.util.List;

public class UserDefinedListViewModel extends AndroidViewModel {

    private final UserDefinedListItemDao mUserDefinedListItemDao;

    public UserDefinedListViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        mUserDefinedListItemDao = db.userDefinedListItemDao();
    }

    public LiveData<List<UserDefinedListItem>> getItemsByListId(int listId) {
        return mUserDefinedListItemDao.getByListId(listId);
    }
}
