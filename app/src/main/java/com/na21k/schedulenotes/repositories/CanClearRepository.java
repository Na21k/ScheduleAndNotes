package com.na21k.schedulenotes.repositories;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

public interface CanClearRepository {

    default Task<Void> deleteAll() {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            deleteAllBlocking();
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    void deleteAllBlocking();
}
