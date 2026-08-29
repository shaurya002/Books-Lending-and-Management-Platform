package com.shaurya.booksmanagementplatform.dto.request;

import jakarta.validation.constraints.NotNull;

public record BorrowRecordRequest(
        @NotNull(message = "BookID is mandatory")
        Long bookId,

        @NotNull(message = "MemberID is mandatory")
        Long memberId
) {
}
