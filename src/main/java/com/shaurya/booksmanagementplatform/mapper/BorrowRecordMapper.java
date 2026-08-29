package com.shaurya.booksmanagementplatform.mapper;

import com.shaurya.booksmanagementplatform.dto.response.BorrowRecordResponse;
import com.shaurya.booksmanagementplatform.model.entity.BorrowRecord;
import org.springframework.stereotype.Component;

@Component
public class BorrowRecordMapper {

    public BorrowRecordResponse toResponse(BorrowRecord borrowRecord) {
        if (borrowRecord == null) {
            return  null;
        }

        return new BorrowRecordResponse(
                borrowRecord.getId(),
                borrowRecord.getBook().getId(),
                borrowRecord.getBook().getTitle(),
                borrowRecord.getMember().getId(),
                borrowRecord.getMember().getFirstName()
                + " " + borrowRecord.getMember().getLastName(),
                borrowRecord.getBorrowDate(),
                borrowRecord.getDueDate(),
                borrowRecord.getReturnDate(),
                borrowRecord.getFine(),
                borrowRecord.getBorrowStatus()
        );
    }
}
