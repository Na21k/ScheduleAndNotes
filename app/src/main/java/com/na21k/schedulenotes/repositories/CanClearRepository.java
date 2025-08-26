package com.na21k.schedulenotes.repositories;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.na21k.schedulenotes.data.database.Identifiable;

//TODO: specify generic type for all the repositories that implement this interface
//(needed to let Dagger know which implementation to inject)
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
