package com.shaurya.booksmanagementplatform.service.impl;

import com.shaurya.booksmanagementplatform.dto.request.BorrowRecordRequest;
import com.shaurya.booksmanagementplatform.dto.response.BorrowRecordResponse;
import com.shaurya.booksmanagementplatform.exception.*;
import com.shaurya.booksmanagementplatform.mapper.BorrowRecordMapper;
import com.shaurya.booksmanagementplatform.model.entity.Book;
import com.shaurya.booksmanagementplatform.model.entity.BorrowRecord;
import com.shaurya.booksmanagementplatform.model.entity.Member;
import com.shaurya.booksmanagementplatform.model.enums.BookStatus;
import com.shaurya.booksmanagementplatform.model.enums.BorrowStatus;
import com.shaurya.booksmanagementplatform.model.enums.MemberStatus;
import com.shaurya.booksmanagementplatform.repositories.BookRepository;
import com.shaurya.booksmanagementplatform.repositories.BorrowRepository;
import com.shaurya.booksmanagementplatform.repositories.MemberRepository;
import com.shaurya.booksmanagementplatform.service.BorrowRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowRecordServiceImpl implements BorrowRecordService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowRecordMapper borrowRecordMapper;

    private static final int MAX_BORROW_LIMIT = 5;
    private static final int BORROW_PERIOD_DAYS = 15;
    private static final double FINE_PER_DAY = 1.0;

    @Override
    @Transactional
    public BorrowRecordResponse borrowBook(BorrowRecordRequest request) {

        //Validating both book & member

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BookNotFoundException("Book by id " + request.bookId() + " not found"));

        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new MemberNotFoundException("Member by id " + request.memberId() + " not found"));

        if(member.getMemberStatus() != MemberStatus.ACTIVE) {
            throw new MemberInactiveException("Member by id " + request.memberId() + " is not active");
        }

        if(book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("Book by id " + request.bookId() + " is not available");
        }

        if(book.getStatus() != BookStatus.AVAILABLE) {
            throw new BookNotAvailableException("Book by id " + request.bookId() + " is not available");
        }

        long activeBorrowCount = borrowRepository.countByMemberAndReturnDateIsNull(member);

        if(activeBorrowCount >= MAX_BORROW_LIMIT) {
            throw new BorrowLimitExceededException("Member by id " + request.memberId() + " has exceeded the borrow limit of " + MAX_BORROW_LIMIT);
        }

        if(borrowRepository.existsByMemberAndBookAndReturnDateIsNull(member, book)) {
            throw new BookAlreadyBorrowedException("Member by id " + request.memberId() + " has already borrowed book by id " + request.bookId());
        }

        // Borrowing the book
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(BORROW_PERIOD_DAYS);

        BorrowRecord borrowRecord = BorrowRecord.builder()
                .book(book)
                .member(member)
                .borrowDate(borrowDate)
                .dueDate(dueDate)
                .returnDate(null)
                .fine(0.0)
                .borrowStatus(BorrowStatus.BORROWED)
                .build();

        // Updating available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);

        if(book.getAvailableCopies() == 0) {
            book.setStatus(BookStatus.OUT_OF_STOCK);
        }
        BorrowRecord savedRecord = borrowRepository.save(borrowRecord);
        return borrowRecordMapper.toResponse(savedRecord);
    }

    @Override
    @Transactional
    public BorrowRecordResponse returnBook(Long borrowRecordId) {
        BorrowRecord borrowRecord = borrowRepository.findById(borrowRecordId)
                .orElseThrow(() -> new BorrowRecordNotFoundException("Borrow record by id " + borrowRecordId + " not found"));

        if(borrowRecord.getReturnDate() != null) {
            throw new BookAlreadyReturnedException("Book for borrow record id " + borrowRecordId + " has already been returned");
        }

        // Returning the book
        LocalDate returnDate = LocalDate.now();
        borrowRecord.setReturnDate(returnDate);

        // Calculating fine if overdue
        if(returnDate.isAfter(borrowRecord.getDueDate())) {
            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(borrowRecord.getDueDate(), returnDate);
            double fine = overdueDays * FINE_PER_DAY;
            borrowRecord.setFine(fine);
        }

        borrowRecord.setBorrowStatus(BorrowStatus.RETURNED);

        // Updating available copies
        Book book = borrowRecord.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        book.setStatus(BookStatus.AVAILABLE);

        BorrowRecord updatedRecord = borrowRepository.save(borrowRecord);
        return borrowRecordMapper.toResponse(updatedRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowRecordResponse getBorrowRecordById(Long borrowRecordId) {
        BorrowRecord borrowRecord = borrowRepository.findById(borrowRecordId)
                .orElseThrow(() -> new BorrowRecordNotFoundException("Borrow record by id " + borrowRecordId + " not found"));
        return borrowRecordMapper.toResponse(borrowRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponse> getBorrowRecordsByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member by id " + memberId + " not found"));
        List<BorrowRecord> borrowRecords = borrowRepository.findByMember(member);

        return borrowRecords.stream()
                .map(borrowRecordMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponse> getBorrowRecordsByBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book by id " + bookId + " not found"));
        List<BorrowRecord> borrowRecords = borrowRepository.findByBook(book);

        return borrowRecords.stream()
                .map(borrowRecordMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponse> getActiveBorrowRecordsByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member by id " + memberId + " not found"));
        List<BorrowRecord> borrowRecords = borrowRepository.findByMemberAndReturnDateIsNull(member);

        return borrowRecords.stream()
                .map(borrowRecordMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponse> getOverdueBorrowRecords() {
        List<BorrowRecord> overdueRecords = borrowRepository.findByDueDateBeforeAndReturnDateIsNull(LocalDate.now());
        return overdueRecords.stream()
                .map(borrowRecordMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponse> getAllBorrowRecords() {
        List<BorrowRecord> borrowRecords = borrowRepository.findAll(Sort.by(Sort.Order.desc("borrowDate")));
        return borrowRecords.stream()
                .map(borrowRecordMapper:: toResponse)
                .toList();
    }

}
