package com.na21k.schedulenotes.repositories;

import com.na21k.schedulenotes.data.database.Identifiable;

public interface CanProvideRandomRepository<T extends Identifiable> {

    T getRandomBlocking();
}
