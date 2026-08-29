package com.shaurya.booksmanagementplatform.dto.response;

import com.shaurya.booksmanagementplatform.model.enums.Role;

public record UserResponse(
        Long id,
        String username,
        Role role,
        Long memberId
) {
}
