package com.shaurya.booksmanagementplatform.controller;

import com.shaurya.booksmanagementplatform.dto.request.BorrowRecordRequest;
import com.shaurya.booksmanagementplatform.dto.response.BorrowRecordResponse;
import com.shaurya.booksmanagementplatform.service.BorrowRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Borrow Records",
        description = "APIs for managing borrow records"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/borrow-records")
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    @Operation(
            summary = "Create a new borrow record",
            description = "Creates a new borrow record entry"
    )
    @PostMapping
    public ResponseEntity<BorrowRecordResponse> createBorrowRecord(@Valid @RequestBody BorrowRecordRequest borrowRecordRequest){
        BorrowRecordResponse response = borrowRecordService.borrowBook(borrowRecordRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Return a borrowed book",
            description = "Updates the status of a borrow record to returned"
    )
    @PutMapping("/{borrowId}/return")
    public ResponseEntity<BorrowRecordResponse> returnBorrowRecord(@PathVariable Long borrowId){
        BorrowRecordResponse response = borrowRecordService.returnBook(borrowId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Get borrow record by ID",
            description = "Retrieves a borrow record by its ID"
    )
    @GetMapping("/{borrowId}")
    public ResponseEntity<BorrowRecordResponse> getBorrowRecord(@PathVariable Long borrowId){
        BorrowRecordResponse response = borrowRecordService.getBorrowRecordById(borrowId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Get all borrow records",
            description = "Retrieves a list of all borrow records"
    )
    @GetMapping
    public ResponseEntity<List<BorrowRecordResponse>> getAllBorrowRecord(){
        List<BorrowRecordResponse> response = borrowRecordService.getAllBorrowRecords();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Get borrow records by member ID",
            description = "Retrieves a list of borrow records for a specific member"
    )
    @GetMapping("/members/{memberId}")
    public ResponseEntity<List<BorrowRecordResponse>> getBorrowRecordsByMemberId(@PathVariable Long memberId){
        List<BorrowRecordResponse> response = borrowRecordService.getBorrowRecordsByMember(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Get borrow records by book ID",
            description = "Retrieves a list of borrow records for a specific book"
    )
    @GetMapping("/books/{bookId}")
    public ResponseEntity<List<BorrowRecordResponse>> getBorrowRecordsByBookId(@PathVariable Long bookId){
        List<BorrowRecordResponse> response = borrowRecordService.getBorrowRecordsByBook(bookId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Get active borrow records by member ID",
            description = "Retrieves a list of active borrow records for a specific member"
    )
    @GetMapping("/members/{memberId}/active")
    public ResponseEntity<List<BorrowRecordResponse>> getActiveRecordsByMemberId(@PathVariable Long memberId){
        List<BorrowRecordResponse> response = borrowRecordService.getActiveBorrowRecordsByMember(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Get overdue borrow records",
            description = "Retrieves a list of overdue borrow records"
    )
    @GetMapping("/overdueRecords")
    public ResponseEntity<List<BorrowRecordResponse>> getOverdueRecords(){
        List<BorrowRecordResponse> response = borrowRecordService.getOverdueBorrowRecords();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
