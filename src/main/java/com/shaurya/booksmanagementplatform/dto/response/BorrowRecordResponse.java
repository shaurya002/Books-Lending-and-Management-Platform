package com.shaurya.booksmanagementplatform.dto.response;

import com.shaurya.booksmanagementplatform.model.enums.BorrowStatus;

import java.time.LocalDate;

public record BorrowRecordResponse(
        Long id,

        Long bookId,
        String bookTitle,

        Long memberId,
        String memberName,

        LocalDate borrowDate,
        LocalDate dueDate,
        LocalDate returnDate,

        Double fine,

        BorrowStatus borrowStatus
) {
}
