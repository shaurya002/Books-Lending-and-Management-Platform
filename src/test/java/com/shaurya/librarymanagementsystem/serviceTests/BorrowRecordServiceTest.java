package com.shaurya.librarymanagementsystem.serviceTests;

import com.shaurya.librarymanagementsystem.dto.request.BorrowRecordRequest;
import com.shaurya.librarymanagementsystem.dto.response.BorrowRecordResponse;
import com.shaurya.librarymanagementsystem.exception.*;
import com.shaurya.librarymanagementsystem.mapper.BorrowRecordMapper;
import com.shaurya.librarymanagementsystem.model.entity.Book;
import com.shaurya.librarymanagementsystem.model.entity.BorrowRecord;
import com.shaurya.librarymanagementsystem.model.entity.Member;
import com.shaurya.librarymanagementsystem.model.enums.BookStatus;
import com.shaurya.librarymanagementsystem.model.enums.BorrowStatus;
import com.shaurya.librarymanagementsystem.model.enums.MemberStatus;
import com.shaurya.librarymanagementsystem.repositories.BookRepository;
import com.shaurya.librarymanagementsystem.repositories.BorrowRepository;
import com.shaurya.librarymanagementsystem.repositories.MemberRepository;
import com.shaurya.librarymanagementsystem.service.impl.BorrowRecordServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BorrowRecordServiceTest {

    @Mock
    private BorrowRepository borrowRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BorrowRecordMapper borrowRecordMapper;

    @InjectMocks
    private BorrowRecordServiceImpl borrowService;

    @Test
    void borrowBook_shouldCreateRecord_whenValid() {
        BorrowRecordRequest req = new BorrowRecordRequest(1L, 2L);
        Book book = Book.builder().id(1L).title("B").totalCopies(2).availableCopies(1).status(BookStatus.AVAILABLE).build();
        Member member = Member.builder().id(2L).firstName("M").lastName("N").memberStatus(MemberStatus.ACTIVE).build();

        BorrowRecord saved = BorrowRecord.builder()
                .id(10L)
                .book(book)
                .member(member)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(15))
                .returnDate(null)
                .fine(0.0)
                .borrowStatus(BorrowStatus.BORROWED)
                .build();

        BorrowRecordResponse expected = new BorrowRecordResponse(10L, 1L, "B", 2L, "M N", saved.getBorrowDate(), saved.getDueDate(), null, 0.0, BorrowStatus.BORROWED);

        when(bookRepository.findById(req.bookId())).thenReturn(Optional.of(book));
        when(memberRepository.findById(req.memberId())).thenReturn(Optional.of(member));
        when(borrowRepository.countByMemberAndReturnDateIsNull(member)).thenReturn(0L);
        when(borrowRepository.existsByMemberAndBookAndReturnDateIsNull(member, book)).thenReturn(false);
        when(borrowRepository.save(any(BorrowRecord.class))).thenReturn(saved);
        when(borrowRecordMapper.toResponse(saved)).thenReturn(expected);

        BorrowRecordResponse actual = borrowService.borrowBook(req);

        assertEquals(expected, actual);
        // available copies decreased
        assertEquals(0, book.getAvailableCopies());
        verify(borrowRepository).save(any(BorrowRecord.class));
        verify(borrowRecordMapper).toResponse(saved);
    }

    @Test
    void borrowBook_shouldThrowBookNotFound_whenMissingBook() {
        BorrowRecordRequest req = new BorrowRecordRequest(1L, 2L);
        when(bookRepository.findById(req.bookId())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> borrowService.borrowBook(req));
        verify(bookRepository).findById(req.bookId());
    }

    @Test
    void borrowBook_shouldThrowMemberNotFound_whenMissingMember() {
        BorrowRecordRequest req = new BorrowRecordRequest(1L, 2L);
        Book book = Book.builder().id(1L).availableCopies(1).status(BookStatus.AVAILABLE).build();
        when(bookRepository.findById(req.bookId())).thenReturn(Optional.of(book));
        when(memberRepository.findById(req.memberId())).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> borrowService.borrowBook(req));
        verify(memberRepository).findById(req.memberId());
    }

    @Test
    void borrowBook_shouldThrowMemberInactive_whenInactive() {
        BorrowRecordRequest req = new BorrowRecordRequest(1L, 2L);
        Book book = Book.builder().id(1L).availableCopies(1).status(BookStatus.AVAILABLE).build();
        Member member = Member.builder().id(2L).memberStatus(MemberStatus.INACTIVE).build();

        when(bookRepository.findById(req.bookId())).thenReturn(Optional.of(book));
        when(memberRepository.findById(req.memberId())).thenReturn(Optional.of(member));

        assertThrows(MemberInactiveException.class, () -> borrowService.borrowBook(req));
        verify(memberRepository).findById(req.memberId());
    }

    @Test
    void borrowBook_shouldThrowBookNotAvailable_whenNoCopies() {
        BorrowRecordRequest req = new BorrowRecordRequest(1L, 2L);
        Book book = Book.builder().id(1L).availableCopies(0).status(BookStatus.AVAILABLE).build();
        Member member = Member.builder().id(2L).memberStatus(MemberStatus.ACTIVE).build();

        when(bookRepository.findById(req.bookId())).thenReturn(Optional.of(book));
        when(memberRepository.findById(req.memberId())).thenReturn(Optional.of(member));

        assertThrows(BookNotAvailableException.class, () -> borrowService.borrowBook(req));
    }

    @Test
    void borrowBook_shouldThrowBookNotAvailable_whenStatusNotAvailable() {
        BorrowRecordRequest req = new BorrowRecordRequest(1L, 2L);
        Book book = Book.builder().id(1L).availableCopies(1).status(BookStatus.OUT_OF_STOCK).build();
        Member member = Member.builder().id(2L).memberStatus(MemberStatus.ACTIVE).build();

        when(bookRepository.findById(req.bookId())).thenReturn(Optional.of(book));
        when(memberRepository.findById(req.memberId())).thenReturn(Optional.of(member));

        assertThrows(BookNotAvailableException.class, () -> borrowService.borrowBook(req));
    }

    @Test
    void borrowBook_shouldThrowBorrowLimitExceeded_whenCountTooHigh() {
        BorrowRecordRequest req = new BorrowRecordRequest(1L, 2L);
        Book book = Book.builder().id(1L).availableCopies(1).status(BookStatus.AVAILABLE).build();
        Member member = Member.builder().id(2L).memberStatus(MemberStatus.ACTIVE).build();

        when(bookRepository.findById(req.bookId())).thenReturn(Optional.of(book));
        when(memberRepository.findById(req.memberId())).thenReturn(Optional.of(member));
        when(borrowRepository.countByMemberAndReturnDateIsNull(member)).thenReturn(5L);

        assertThrows(BorrowLimitExceededException.class, () -> borrowService.borrowBook(req));
    }

    @Test
    void borrowBook_shouldThrowBookAlreadyBorrowed_whenAlreadyBorrowed() {
        BorrowRecordRequest req = new BorrowRecordRequest(1L, 2L);
        Book book = Book.builder().id(1L).availableCopies(1).status(BookStatus.AVAILABLE).build();
        Member member = Member.builder().id(2L).memberStatus(MemberStatus.ACTIVE).build();

        when(bookRepository.findById(req.bookId())).thenReturn(Optional.of(book));
        when(memberRepository.findById(req.memberId())).thenReturn(Optional.of(member));
        when(borrowRepository.countByMemberAndReturnDateIsNull(member)).thenReturn(0L);
        when(borrowRepository.existsByMemberAndBookAndReturnDateIsNull(member, book)).thenReturn(true);

        assertThrows(BookAlreadyBorrowedException.class, () -> borrowService.borrowBook(req));
    }

    @Test
    void returnBook_shouldReturnAndComputeFine_whenOverdue() {
        Long borrowId = 20L;
        Book book = Book.builder().id(1L).title("T").availableCopies(0).status(BookStatus.OUT_OF_STOCK).build();
        Member member = Member.builder().id(2L).firstName("M").lastName("L").build();
        BorrowRecord record = BorrowRecord.builder()
                .id(borrowId)
                .book(book)
                .member(member)
                .borrowDate(LocalDate.now().minusDays(20))
                .dueDate(LocalDate.now().minusDays(5))
                .returnDate(null)
                .fine(0.0)
                .borrowStatus(BorrowStatus.BORROWED)
                .build();

        BorrowRecord updated = BorrowRecord.builder()
                .id(borrowId)
                .book(book)
                .member(member)
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .returnDate(LocalDate.now())
                .fine(5.0)
                .borrowStatus(BorrowStatus.RETURNED)
                .build();

        BorrowRecordResponse expected = new BorrowRecordResponse(borrowId, 1L, "T", 2L, "M L", record.getBorrowDate(), record.getDueDate(), updated.getReturnDate(), 5.0, BorrowStatus.RETURNED);

        when(borrowRepository.findById(borrowId)).thenReturn(Optional.of(record));
        when(borrowRepository.save(any(BorrowRecord.class))).thenReturn(updated);
        when(borrowRecordMapper.toResponse(updated)).thenReturn(expected);

        BorrowRecordResponse actual = borrowService.returnBook(borrowId);

        assertEquals(expected, actual);
        // book available copies incremented and status set
        assertEquals(1, book.getAvailableCopies());
        assertThat(book.getStatus()).isEqualTo(BookStatus.AVAILABLE);
        verify(borrowRepository).save(any(BorrowRecord.class));
    }

    @Test
    void returnBook_shouldThrowNotFound_whenMissing() {
        Long id = 300L;
        when(borrowRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BorrowRecordNotFoundException.class, () -> borrowService.returnBook(id));
        verify(borrowRepository).findById(id);
    }

    @Test
    void returnBook_shouldThrowAlreadyReturned_whenReturnDateExists() {
        Long id = 21L;
        BorrowRecord record = BorrowRecord.builder().id(id).returnDate(LocalDate.now().minusDays(1)).build();
        when(borrowRepository.findById(id)).thenReturn(Optional.of(record));

        assertThrows(BookAlreadyReturnedException.class, () -> borrowService.returnBook(id));
    }

    @Test
    void getBorrowRecordById_shouldReturnResponse_whenFound() {
        Long id = 50L;
        Book book = Book.builder().id(5L).title("X").build();
        Member member = Member.builder().id(6L).firstName("A").lastName("B").build();
        BorrowRecord record = BorrowRecord.builder().id(id).book(book).member(member).build();
        BorrowRecordResponse expected = new BorrowRecordResponse(id, 5L, "X", 6L, "A B", null, null, null, 0.0, null);

        when(borrowRepository.findById(id)).thenReturn(Optional.of(record));
        when(borrowRecordMapper.toResponse(record)).thenReturn(expected);

        BorrowRecordResponse actual = borrowService.getBorrowRecordById(id);
        assertEquals(expected, actual);
        verify(borrowRepository).findById(id);
        verify(borrowRecordMapper).toResponse(record);
    }

    @Test
    void getBorrowRecordById_shouldThrowNotFound_whenMissing() {
        Long id = 60L;
        when(borrowRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BorrowRecordNotFoundException.class, () -> borrowService.getBorrowRecordById(id));
    }

    @Test
    void getBorrowRecordsByMember_shouldReturnMappedList_whenExists() {
        Long memberId = 7L;
        Member member = Member.builder().id(memberId).build();
        BorrowRecord r1 = BorrowRecord.builder().id(1L).member(member).build();
        BorrowRecordResponse resp1 = new BorrowRecordResponse(1L, null, null, memberId, null, null, null, null, 0.0, null);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(borrowRepository.findByMember(member)).thenReturn(List.of(r1));
        when(borrowRecordMapper.toResponse(r1)).thenReturn(resp1);

        List<BorrowRecordResponse> res = borrowService.getBorrowRecordsByMember(memberId);
        assertEquals(1, res.size());
        assertEquals(resp1, res.get(0));
    }

    @Test
    void getBorrowRecordsByBook_shouldReturnMappedList_whenExists() {
        Long bookId = 8L;
        Book book = Book.builder().id(bookId).build();
        BorrowRecord r = BorrowRecord.builder().id(2L).book(book).build();
        BorrowRecordResponse resp = new BorrowRecordResponse(2L, bookId, null, null, null, null, null, null, 0.0, null);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(borrowRepository.findByBook(book)).thenReturn(List.of(r));
        when(borrowRecordMapper.toResponse(r)).thenReturn(resp);

        List<BorrowRecordResponse> res = borrowService.getBorrowRecordsByBook(bookId);
        assertEquals(1, res.size());
        assertEquals(resp, res.get(0));
    }

    @Test
    void getActiveBorrowRecordsByMember_shouldReturnMappedList() {
        Long memberId = 9L;
        Member member = Member.builder().id(memberId).build();
        BorrowRecord r = BorrowRecord.builder().id(3L).member(member).returnDate(null).build();
        BorrowRecordResponse resp = new BorrowRecordResponse(3L, null, null, memberId, null, null, null, null, 0.0, null);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(borrowRepository.findByMemberAndReturnDateIsNull(member)).thenReturn(List.of(r));
        when(borrowRecordMapper.toResponse(r)).thenReturn(resp);

        List<BorrowRecordResponse> res = borrowService.getActiveBorrowRecordsByMember(memberId);
        assertEquals(1, res.size());
        assertEquals(resp, res.get(0));
    }

    @Test
    void getOverdueBorrowRecords_shouldReturnMappedList() {
        BorrowRecord r = BorrowRecord.builder().id(4L).dueDate(LocalDate.now().minusDays(1)).returnDate(null).build();
        BorrowRecordResponse resp = new BorrowRecordResponse(4L, null, null, null, null, null, r.getDueDate(), null, 0.0, null);

        when(borrowRepository.findByDueDateBeforeAndReturnDateIsNull(LocalDate.now())).thenReturn(List.of(r));
        when(borrowRecordMapper.toResponse(r)).thenReturn(resp);

        List<BorrowRecordResponse> res = borrowService.getOverdueBorrowRecords();
        assertEquals(1, res.size());
        assertEquals(resp, res.get(0));
    }

    @Test
    void getAllBorrowRecords_shouldReturnMappedList() {
        BorrowRecord r = BorrowRecord.builder().id(5L).borrowDate(LocalDate.now()).build();
        BorrowRecordResponse resp = new BorrowRecordResponse(5L, null, null, null, null, r.getBorrowDate(), null, null, 0.0, null);

        when(borrowRepository.findAll(Sort.by(Sort.Order.desc("borrowDate")))).thenReturn(List.of(r));
        when(borrowRecordMapper.toResponse(r)).thenReturn(resp);

        List<BorrowRecordResponse> res = borrowService.getAllBorrowRecords();
        assertEquals(1, res.size());
        assertEquals(resp, res.get(0));
    }
}
