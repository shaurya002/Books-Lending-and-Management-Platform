package com.shaurya.booksmanagementplatform.dto.response;

import com.shaurya.booksmanagementplatform.model.enums.BookStatus;

import java.util.List;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        String genre,
        Integer publishedYear,
        Integer totalCopies,
        Integer availableCopies,
        BookStatus status,
        List<String> authorsNames
) {
}
