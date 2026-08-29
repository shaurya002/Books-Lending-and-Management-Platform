package com.shaurya.booksmanagementplatform.exception;

public class DuplicateAuthorException extends RuntimeException {
    public DuplicateAuthorException(String message) {
        super(message);
    }
}
