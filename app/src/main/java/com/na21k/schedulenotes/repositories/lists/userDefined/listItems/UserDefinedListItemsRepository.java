package com.na21k.schedulenotes.repositories.lists.userDefined.listItems;

import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.Lists.UserDefined.UserDefinedListItem;

import java.util.List;

public interface UserDefinedListItemsRepository {

    LiveData<List<UserDefinedListItem>> getAllForList(int listId);

    LiveData<List<UserDefinedListItem>> getSearch(int listId, String query);
}
