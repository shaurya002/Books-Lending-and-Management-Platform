package com.shaurya.booksmanagementplatform.dto.request;

import com.shaurya.booksmanagementplatform.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotBlank
        String username,

        @NotBlank
        String password,

        @NotNull
        Role role,

        Long memberId
) {
}
