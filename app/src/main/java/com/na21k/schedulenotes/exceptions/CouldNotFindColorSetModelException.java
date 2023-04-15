package com.na21k.schedulenotes.exceptions;

import androidx.annotation.NonNull;

import com.na21k.schedulenotes.data.models.ColorSet;

public class CouldNotFindColorSetModelException extends RuntimeException {

    public CouldNotFindColorSetModelException(String message, @NonNull ColorSet colorSet) {
        super(message + colorSet);
    }
}
