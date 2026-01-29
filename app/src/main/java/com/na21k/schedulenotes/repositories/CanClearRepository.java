package com.na21k.schedulenotes.repositories;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.na21k.schedulenotes.data.database.Identifiable;

//the <T> is needed for Dagger
public interface CanClearRepository<T extends Identifiable> {

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
