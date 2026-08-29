package com.shaurya.booksmanagementplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record BookRequest(
        @NotBlank(message = "Title is mandatory")
        String title,

        @NotBlank(message = "ISBN is mandatory")
        String isbn,

        String genre,

        Integer publishedYear,

        @Positive(message = "Total copies must be positive")
        Integer totalCopies,

        @NotEmpty(message = "At least one author is required")
        List<Long> authorIds
) {
}
