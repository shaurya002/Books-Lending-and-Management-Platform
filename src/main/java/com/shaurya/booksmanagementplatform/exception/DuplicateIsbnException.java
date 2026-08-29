package com.shaurya.booksmanagementplatform.exception;

public class DuplicateIsbnException extends RuntimeException{
    public DuplicateIsbnException(String message){
        super(message);
    }
}
