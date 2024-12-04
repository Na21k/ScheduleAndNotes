package com.na21k.schedulenotes.data.database;

public interface CanProvideRandomDao<TEntity extends Identifiable> {

    TEntity getRandomBlocking();
}
