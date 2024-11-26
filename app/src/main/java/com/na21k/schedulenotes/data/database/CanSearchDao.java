package com.na21k.schedulenotes.data.database;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface CanSearchDao<TEntity extends Identifiable> {

    LiveData<List<TEntity>> search(String query);
}
