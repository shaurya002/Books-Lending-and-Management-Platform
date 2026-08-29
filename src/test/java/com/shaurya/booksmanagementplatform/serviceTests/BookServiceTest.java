package com.shaurya.booksmanagementplatform.serviceTests;

import com.shaurya.booksmanagementplatform.dto.request.BookRequest;
import com.shaurya.booksmanagementplatform.dto.response.BookResponse;
import com.shaurya.booksmanagementplatform.dto.response.PageResponse;
import com.shaurya.booksmanagementplatform.exception.AuthorNotFoundException;
import com.shaurya.booksmanagementplatform.exception.BookNotFoundException;
import com.shaurya.booksmanagementplatform.exception.DuplicateIsbnException;
import com.shaurya.booksmanagementplatform.mapper.BookMapper;
import com.shaurya.booksmanagementplatform.model.entity.Author;
import com.shaurya.booksmanagementplatform.model.entity.Book;
import com.shaurya.booksmanagementplatform.model.enums.BookStatus;
import com.shaurya.booksmanagementplatform.repositories.AuthorRepository;
import com.shaurya.booksmanagementplatform.repositories.BookRepository;
import com.shaurya.booksmanagementplatform.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void createBook_shouldSaveAndReturnResponse_whenRequestIsValid() {
        // Arrange
        BookRequest request = new BookRequest(
                "Effective Java",
                "978-0134685991",
                "Programming",
                2018,
                3,
                List.of(1L)
        );

        Author author = Author.builder().id(1L).name("Joshua Bloch").build();

        Book mappedEntity = Book.builder()
                .title(request.title())
                .isbn(request.isbn())
                .genre(request.genre())
                .publishedYear(request.publishedYear())
                .totalCopies(request.totalCopies())
                .availableCopies(request.totalCopies())
                .status(BookStatus.AVAILABLE)
                .authors(Set.of(author))
                .build();

        Book savedEntity = Book.builder()
                .id(10L)
                .title(mappedEntity.getTitle())
                .isbn(mappedEntity.getIsbn())
                .genre(mappedEntity.getGenre())
                .publishedYear(mappedEntity.getPublishedYear())
                .totalCopies(mappedEntity.getTotalCopies())
                .availableCopies(mappedEntity.getAvailableCopies())
                .status(mappedEntity.getStatus())
                .authors(mappedEntity.getAuthors())
                .build();

        BookResponse expectedResponse = new BookResponse(
                savedEntity.getId(),
                savedEntity.getTitle(),
                savedEntity.getIsbn(),
                savedEntity.getGenre(),
                savedEntity.getPublishedYear(),
                savedEntity.getTotalCopies(),
                savedEntity.getAvailableCopies(),
                savedEntity.getStatus(),
                List.of(author.getName())
        );

        when(bookRepository.existsByIsbn(request.isbn())).thenReturn(false);
        when(authorRepository.findAllById(request.authorIds())).thenReturn(List.of(author));
        when(bookMapper.toEntity(eq(request), any())).thenReturn(mappedEntity);
        when(bookRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(bookMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        // Act
        BookResponse actual = bookService.createBook(request);

        // Assert
        assertThat(actual).isEqualTo(expectedResponse);

        // Verify interactions
        verify(bookRepository).existsByIsbn(request.isbn());
        verify(authorRepository).findAllById(request.authorIds());
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());

        Book captured = bookCaptor.getValue();
        assertThat(captured.getIsbn()).isEqualTo(request.isbn());
        assertThat(captured.getTotalCopies()).isEqualTo(request.totalCopies());
        assertThat(captured.getAvailableCopies()).isEqualTo(request.totalCopies());

        verify(bookMapper).toResponse(savedEntity);
        verifyNoMoreInteractions(bookRepository, authorRepository, bookMapper);
    }

    @Test
    void createBook_shouldThrowDuplicateIsbnException_whenIsbnAlreadyExists() {
        BookRequest request = new BookRequest("Any", "dup-isbn", "G", 2000, 1, List.of(1L));
        when(bookRepository.existsByIsbn(request.isbn())).thenReturn(true);

        assertThrows(DuplicateIsbnException.class, () -> bookService.createBook(request));

        verify(bookRepository).existsByIsbn(request.isbn());
        verifyNoMoreInteractions(bookRepository, authorRepository, bookMapper);
    }

    @Test
    void createBook_shouldThrowAuthorNotFoundException_whenAuthorIdsMismatch() {
        BookRequest request = new BookRequest("Any", "isbn", "G", 2000, 1, List.of(1L, 2L));
        when(bookRepository.existsByIsbn(request.isbn())).thenReturn(false);
        when(authorRepository.findAllById(request.authorIds())).thenReturn(List.of(Author.builder().id(1L).name("A").build()));

        assertThrows(AuthorNotFoundException.class, () -> bookService.createBook(request));

        verify(bookRepository).existsByIsbn(request.isbn());
        verify(authorRepository).findAllById(request.authorIds());
        verifyNoMoreInteractions(bookRepository, authorRepository, bookMapper);
    }

    @Test
    void updateBook_shouldUpdateExistingBookAndReturnResponse_whenRequestIsValid() {
        Long bookId = 10L;
        BookRequest request = new BookRequest(
                "Effective Java 3rd Edition",
                "978-0134685991",
                "Programming",
                2018,
                5,
                List.of(1L)
        );
        Author author = Author.builder()
                .id(1L)
                .name("Joshua Bloch")
                .build();

        Book existingBook = Book.builder()
                .id(bookId)
                .title("Effective Java")
                .isbn("978-0134685991")
                .genre("Programming")
                .publishedYear(2018)
                .totalCopies(3)
                .availableCopies(3)
                .status(BookStatus.AVAILABLE)
                .authors(Set.of(author))
                .build();

        Book updatedBook = Book.builder()
                .id(bookId)
                .title(request.title())
                .isbn(request.isbn())
                .genre(request.genre())
                .publishedYear(request.publishedYear())
                .totalCopies(request.totalCopies())
                .availableCopies(request.totalCopies())
                .status(BookStatus.AVAILABLE)
                .authors(Set.of(author))
                .build();

        BookResponse expectedResponse = new BookResponse(
                updatedBook.getId(),
                updatedBook.getTitle(),
                updatedBook.getIsbn(),
                updatedBook.getGenre(),
                updatedBook.getPublishedYear(),
                updatedBook.getTotalCopies(),
                updatedBook.getAvailableCopies(),
                updatedBook.getStatus(),
                List.of(author.getName())
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(authorRepository.findAllById(request.authorIds())).thenReturn(List.of(author));
        when(bookRepository.save(existingBook)).thenReturn(updatedBook);
        when(bookMapper.toResponse(updatedBook)).thenReturn(expectedResponse);

        BookResponse actual = bookService.updateBook(bookId, request);

        assertThat(actual).isEqualTo(expectedResponse);

        verify(bookRepository).findById(bookId);
        verify(authorRepository).findAllById(request.authorIds());
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());
        Book captured = bookCaptor.getValue();
        assertThat(captured.getTitle()).isEqualTo(request.title());
        assertThat(captured.getIsbn()).isEqualTo(request.isbn());
        assertThat(captured.getGenre()).isEqualTo(request.genre());
        assertThat(captured.getPublishedYear()).isEqualTo(request.publishedYear());
        assertThat(captured.getTotalCopies()).isEqualTo(request.totalCopies());
        assertThat(captured.getAvailableCopies()).isEqualTo(request.totalCopies());
        assertThat(captured.getAuthors()).contains(author);

        verify(bookMapper).toResponse(updatedBook);
    }

    @Test
    void updateBook_shouldThrowBookNotFoundException_whenBookDoesNotExist() {
        Long bookId = 99L;
        BookRequest request = new BookRequest("T", "isbn", "g", 2000, 1, null);
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(bookId, request));

        verify(bookRepository).findById(bookId);
        verifyNoMoreInteractions(bookRepository, authorRepository, bookMapper);
    }

    @Test
    void updateBook_shouldThrowDuplicateIsbnException_whenNewIsbnAlreadyExists() {
        Long bookId = 10L;
        Book existing = Book.builder().id(bookId).isbn("old-isbn").totalCopies(1).availableCopies(1).build();
        BookRequest request = new BookRequest(null, "new-isbn", null, null, null, null);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existing));
        when(bookRepository.existsByIsbn(request.isbn())).thenReturn(true);

        assertThrows(DuplicateIsbnException.class, () -> bookService.updateBook(bookId, request));

        verify(bookRepository).findById(bookId);
        verify(bookRepository).existsByIsbn(request.isbn());
        verifyNoMoreInteractions(bookRepository, authorRepository, bookMapper);
    }

    @Test
    void updateBook_shouldThrowAuthorNotFoundException_whenAuthorIdsMismatch() {
        Long bookId = 10L;
        Book existing = Book.builder().id(bookId).isbn("isbn").totalCopies(1).availableCopies(1).build();
        BookRequest request = new BookRequest(null, null, null, null, null, List.of(1L,2L));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existing));
        when(authorRepository.findAllById(request.authorIds())).thenReturn(List.of(Author.builder().id(1L).name("A").build()));

        assertThrows(AuthorNotFoundException.class, () -> bookService.updateBook(bookId, request));

        verify(bookRepository).findById(bookId);
        verify(authorRepository).findAllById(request.authorIds());
        verifyNoMoreInteractions(bookRepository, authorRepository, bookMapper);
    }

    @Test
    void deleteBook_shouldDeletedBookAndReturnResponse_whenRequestIsValid() {
        Long bookId = 10L;
        Book existingBook = Book.builder()
                .id(bookId)
                .title("Effective Java")
                .isbn("978-0134685991")
                .genre("Programming")
                .publishedYear(2018)
                .totalCopies(3)
                .availableCopies(3)
                .status(BookStatus.AVAILABLE)
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        bookService.deleteBook(bookId);

        verify(bookRepository).delete(existingBook);
    }

    @Test
    void deleteBook_shouldThrowBookNotFoundException_whenBookNotFound() {
        Long bookId = 99L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(bookId));

        verify(bookRepository).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void getBookById_shouldFetchBookAndReturnResponse_whenRequestIsValid(){
        Long bookId = 10L;
        Book existingBook = Book.builder()
                .id(bookId)
                .title("Effective Java")
                .isbn("978-0134685991")
                .genre("Programming")
                .publishedYear(2018)
                .totalCopies(3)
                .availableCopies(3)
                .status(BookStatus.AVAILABLE)
                .build();
        BookResponse expectedResponse = new BookResponse(
                bookId,
                "Effective Java",
                "978-0134685991",
                "Programming",
                2018,
                3,
                3,
                BookStatus.AVAILABLE,
                List.of()
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookMapper.toResponse(existingBook)).thenReturn(expectedResponse);

        BookResponse actualResponse = bookService.getBookById(bookId);

        assertEquals(expectedResponse, actualResponse);

        verify(bookRepository).findById(bookId);
        verify(bookMapper).toResponse(existingBook);
    }

    @Test
    void getBookById_shouldThrowBookNotFoundException_whenNotFound(){
        Long bookId = 99L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(bookId));

        verify(bookRepository).findById(bookId);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void findByTitle_shouldFetchBooksAndReturnResponse_whenRequestIsValid(){
        //given
        Long bookId1 = 10L;
        Book existingBook1 = Book.builder()
                .id(bookId1)
                .title("Effective Java")
                .isbn("978-0134685991")
                .genre("Programming")
                .publishedYear(2018)
                .totalCopies(3)
                .availableCopies(3)
                .status(BookStatus.AVAILABLE)
                .build();

        Long bookId2 = 11L;
        Book existingBook2 = Book.builder()
                .id(bookId2)
                .title("Effective means of Communication")
                .isbn("978-0134685992")
                .genre("Communication")
                .publishedYear(2010)
                .totalCopies(10)
                .availableCopies(3)
                .status(BookStatus.AVAILABLE)
                .build();

        List<Book> existingBooks = List.of(existingBook1, existingBook2);
        Page<Book> bookPage = new PageImpl<>(existingBooks);

        BookResponse expectedResponse1 = new BookResponse(
                bookId1,
                "Effective Java",
                "978-0134685991",
                "Programming",
                2018,
                3,
                3,
                BookStatus.AVAILABLE,
                List.of()
        );
        BookResponse expectedResponse2 = new BookResponse(
                bookId2,
                "Effective means of Communication",
                "978-0134685992",
                "Communication",
                2010,
                10,
                3,
                BookStatus.AVAILABLE,
                List.of()
        );

        when(bookRepository.findByTitleContainingIgnoreCase(eq("Effective"), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.toResponse(existingBook1)).thenReturn(expectedResponse1);
        when(bookMapper.toResponse(existingBook2)).thenReturn(expectedResponse2);

        PageResponse<BookResponse> actualResponse = bookService.findByTitle("Effective",0);

        assertEquals(2, actualResponse.content().size());
        assertEquals(expectedResponse1, actualResponse.content().get(0));
        assertEquals(expectedResponse2, actualResponse.content().get(1));

        verify(bookMapper).toResponse(existingBook1);
        verify(bookMapper).toResponse(existingBook2);
    }

    @Test
    void findByTitle_shouldReturnEmpty_whenNoResults(){
        Page<Book> emptyPage = new PageImpl<>(List.of());
        when(bookRepository.findByTitleContainingIgnoreCase(eq("Nope"), any(Pageable.class))).thenReturn(emptyPage);

        PageResponse<BookResponse> resp = bookService.findByTitle("Nope",0);
        assertEquals(0, resp.content().size());

        verifyNoMoreInteractions(bookMapper);
    }

    @Test
    void findByAuthors_NameContaining_shouldFetchAuthorsAndReturnResponse_whenRequestIsValid(){
        Long bookId = 10L;
        Author author = Author.builder().id(1L).name("John Doe").build();
        Book book = Book.builder().id(bookId).title("A Book").authors(Set.of(author)).build();
        Page<Book> page = new PageImpl<>(List.of(book));

        BookResponse expected = new BookResponse(bookId, "A Book", null, null, null, null, null, null, List.of("John Doe"));

        when(bookRepository.findByAuthors_NameContaining(eq("John"), any(Pageable.class))).thenReturn(page);
        when(bookMapper.toResponse(book)).thenReturn(expected);

        PageResponse<BookResponse> resp = bookService.findByAuthors_NameContaining(0, "John");

        assertEquals(1, resp.content().size());
        assertEquals(expected, resp.content().get(0));

        verify(bookMapper).toResponse(book);
    }

    @Test
    void findByAuthors_NameContaining_shouldReturnEmpty_whenNoResults(){
        Page<Book> empty = new PageImpl<>(List.of());
        when(bookRepository.findByAuthors_NameContaining(eq("Nobody"), any(Pageable.class))).thenReturn(empty);

        PageResponse<BookResponse> resp = bookService.findByAuthors_NameContaining(0, "Nobody");
        assertEquals(0, resp.content().size());
    }

    @Test
    void findByIsbn_shouldReturnResponse_whenFound(){
        String isbn = "isbn-1";
        Book book = Book.builder().id(5L).isbn(isbn).title("T").build();
        BookResponse expected = new BookResponse(5L, "T", isbn, null, null, null, null, null, List.of());

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
        when(bookMapper.toResponse(book)).thenReturn(expected);

        BookResponse actual = bookService.findByIsbn(isbn);
        assertEquals(expected, actual);

        verify(bookRepository).findByIsbn(isbn);
        verify(bookMapper).toResponse(book);
    }

    @Test
    void findByIsbn_shouldThrowBookNotFoundException_whenNotFound(){
        String isbn = "missing";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.findByIsbn(isbn));

        verify(bookRepository).findByIsbn(isbn);
        verifyNoMoreInteractions(bookMapper);
    }
}
