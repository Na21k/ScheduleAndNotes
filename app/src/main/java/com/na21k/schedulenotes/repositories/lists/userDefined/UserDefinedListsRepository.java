package com.na21k.schedulenotes.repositories.lists.userDefined;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedList;
import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListDao;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.util.List;

import javax.inject.Inject;

public class UserDefinedListsRepository extends MutableRepository<UserDefinedList>
        implements CanSearchRepository<UserDefinedList>, CanClearRepository {

    private final UserDefinedListDao mUserDefinedListDao = db.userDefinedListDao();

    @Inject
    public UserDefinedListsRepository(@NonNull Context context) {
        super(context);
    }

    @Override
    public LiveData<List<UserDefinedList>> getSearch(String query) {
        return mUserDefinedListDao.search(query);
    }

    @Override
    public void clearBlocking() {
        mUserDefinedListDao.deleteAll();
    }

    @Override
    protected BaseDao<UserDefinedList> getDao() {
        return mUserDefinedListDao;
    }
}
