package com.shaurya.booksmanagementplatform.dto.response;

import com.shaurya.booksmanagementplatform.model.enums.MemberStatus;

import java.time.LocalDate;

public record MemberResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate membershipDate,
        MemberStatus memberStatus
) {
}
