package com.shaurya.booksmanagementplatform.exception;

import com.shaurya.booksmanagementplatform.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthorNotFoundException(AuthorNotFoundException e, HttpServletRequest request){
        ApiErrorResponse response = new ApiErrorResponse(
                                        LocalDateTime.now(),
                                        HttpStatus.NOT_FOUND.value(),
                                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                                        e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBookNotFoundException(BookNotFoundException e, HttpServletRequest request){
        ApiErrorResponse response = new ApiErrorResponse(
                                        LocalDateTime.now(),
                                        HttpStatus.NOT_FOUND.value(),
                                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                                        e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(AuthorDeletionException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthorDeletionException(AuthorDeletionException e, HttpServletRequest request){
        ApiErrorResponse response = new ApiErrorResponse(
                                        LocalDateTime.now(),
                                        HttpStatus.CONFLICT.value(),
                                        HttpStatus.CONFLICT.getReasonPhrase(),
                                        e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(DuplicateAuthorException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateAuthorException(DuplicateAuthorException e, HttpServletRequest request){
        ApiErrorResponse response = new ApiErrorResponse(
                                        LocalDateTime.now(),
                                        HttpStatus.CONFLICT.value(),
                                        HttpStatus.CONFLICT.getReasonPhrase(),
                                        e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(DuplicateIsbnException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateIsbnException(DuplicateIsbnException e, HttpServletRequest request){
        ApiErrorResponse response = new ApiErrorResponse(
                                        LocalDateTime.now(),
                                        HttpStatus.CONFLICT.value(),
                                        HttpStatus.CONFLICT.getReasonPhrase(),
                                        e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateEmailException(DuplicateEmailException e, HttpServletRequest request){
        ApiErrorResponse response = new ApiErrorResponse(
                                        LocalDateTime.now(),
                                        HttpStatus.CONFLICT.value(),
                                        HttpStatus.CONFLICT.getReasonPhrase(),
                                        e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleMemberNotFoundException(MemberNotFoundException e, HttpServletRequest request){
        ApiErrorResponse response = new ApiErrorResponse(
                                        LocalDateTime.now(),
                                        HttpStatus.NOT_FOUND.value(),
                                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                                        e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MemberInactiveException.class)
    public ResponseEntity<ApiErrorResponse> handleMemberInactiveException(MemberInactiveException e, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<ApiErrorResponse> handleBookNotAvailableException(BookNotAvailableException e, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BorrowLimitExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleBorrowLimitExceededException(BorrowLimitExceededException e, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BorrowRecordNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBorrowRecordNotFoundException(BorrowRecordNotFoundException e, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BookAlreadyBorrowedException.class)
    public ResponseEntity<ApiErrorResponse> handleBookAlreadyBorrowedException(BookAlreadyBorrowedException e, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                e.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BookAlreadyReturnedException.class)
    public ResponseEntity<ApiErrorResponse> handleBookAlreadyReturnedException(BookAlreadyReturnedException e, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                e.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InvalidMembershipDateRangeException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidMembershipDateRangeException(InvalidMembershipDateRangeException e, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
