package com.na21k.schedulenotes.ui.shared;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.na21k.schedulenotes.exceptions.WrongFactoryException;

public abstract class BaseViewModelFactory implements ViewModelProvider.Factory {

    protected final void ensureViewModelType(@NonNull ViewModel vm,
                                             @NonNull Class<?> requestedModelClass) {
        if (!requestedModelClass.isInstance(vm)) {
            throw new WrongFactoryException(requestedModelClass);
        }
    }
}
