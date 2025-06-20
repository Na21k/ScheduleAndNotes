package com.na21k.schedulenotes.exceptions;

public class WrongFactoryException extends RuntimeException {

    public WrongFactoryException(Class<?> requestedClass) {
        super("You're using the wrong Factory. This Factory cannot instantiate " + requestedClass);
    }
}
