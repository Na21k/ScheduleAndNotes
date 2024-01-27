package com.na21k.schedulenotes.repositories;

import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.Identifiable;

import java.util.List;

public interface CanListRepository<T extends Identifiable> {

    LiveData<List<T>> getAll();

    LiveData<T> getById(int id);
}
