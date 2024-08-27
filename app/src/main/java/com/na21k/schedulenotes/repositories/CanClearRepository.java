package com.na21k.schedulenotes.repositories;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

public interface CanClearRepository {

    default Task<Void> clear() {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            clearBlocking();
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    void clearBlocking();
}
