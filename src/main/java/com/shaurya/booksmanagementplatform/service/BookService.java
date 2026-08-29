package com.shaurya.booksmanagementplatform.service;

import com.shaurya.booksmanagementplatform.dto.request.BookRequest;
import com.shaurya.booksmanagementplatform.dto.response.BookResponse;
import com.shaurya.booksmanagementplatform.dto.response.PageResponse;
import com.shaurya.booksmanagementplatform.model.enums.BookStatus;

import java.util.List;

public interface BookService {

    BookResponse createBook(BookRequest request);
    BookResponse updateBook(Long id, BookRequest request);
    void deleteBook(Long id);
    BookResponse getBookById(Long id);
    PageResponse<BookResponse> findByTitle(String title, int page);
    PageResponse<BookResponse> findByPublishedYearBetween(int page, Integer startYear, Integer endYear);
    PageResponse<BookResponse> findByAuthors_NameContaining(int page, String authorName);
    PageResponse<BookResponse> findByGenre(int page, String genre);
    PageResponse<BookResponse> findByStatus(BookStatus status, int page);
    BookResponse findByIsbn(String isbn);
    PageResponse<BookResponse> getAllBooks(int page);

}
