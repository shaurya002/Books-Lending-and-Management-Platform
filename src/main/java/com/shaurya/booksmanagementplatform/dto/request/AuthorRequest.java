package com.shaurya.booksmanagementplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorRequest(
        @NotBlank(message = "Author name is required")
        String name,

        @Size(max = 2000, message = "Biography must not exceed 2000 characters")
        String biography
) {
}
