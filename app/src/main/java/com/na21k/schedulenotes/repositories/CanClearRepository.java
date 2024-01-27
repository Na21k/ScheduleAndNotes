package com.na21k.schedulenotes.repositories;

import com.google.android.gms.tasks.Task;

public interface CanClearRepository {

    Task<Void> deleteAll();
}
