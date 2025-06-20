package com.na21k.schedulenotes.repositories.lists.userDefined;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItemDao;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

import javax.inject.Inject;

public class UserDefinedListItemsRepository extends MutableRepository<UserDefinedListItem> {

    private final UserDefinedListItemDao mUserDefinedListItemDao = db.userDefinedListItemDao();

    @Inject
    public UserDefinedListItemsRepository(@NonNull Context context) {
        super(context);
    }

    public LiveData<List<UserDefinedListItem>> getAllForList(int listId) {
        return mUserDefinedListItemDao.getByListId(listId);
    }

    public LiveData<List<UserDefinedListItem>> getSearch(int listId, String query) {
        return mUserDefinedListItemDao.searchInList(listId, query);
    }

    @Override
    protected BaseDao<UserDefinedListItem> getDao() {
        return mUserDefinedListItemDao;
    }
}
