package com.shaurya.booksmanagementplatform.service.impl;

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
import com.shaurya.booksmanagementplatform.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final AuthorRepository authorRepository;

    private final int pageSize = 7; // Default page size

    @Override
    @Transactional
    public BookResponse createBook(BookRequest bookRequest){
        if(bookRepository.existsByIsbn(bookRequest.isbn())){
            throw new DuplicateIsbnException("Book with ISBN " + bookRequest.isbn() + " already exists.");
        }
        List<Author> authors = authorRepository.findAllById(bookRequest.authorIds());
        if(authors.size()!=bookRequest.authorIds().size()){
            throw new AuthorNotFoundException("Author Ids do not match.");
        }
        Book book = bookMapper.toEntity(
                bookRequest,
                new HashSet<>(authors)
        );

        book.setAvailableCopies(book.getTotalCopies());
        book.setStatus(BookStatus.AVAILABLE);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, BookRequest request){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        if(request.isbn() != null && !request.isbn().equals(book.getIsbn())){
            if(bookRepository.existsByIsbn(request.isbn())){
                throw new DuplicateIsbnException("Book with ISBN " + request.isbn() + " already exists.");
            }
            book.setIsbn(request.isbn());
        }

        if(request.title() != null){
            book.setTitle(request.title());
        }

        if(request.genre() != null){
            book.setGenre(request.genre());
        }

        if(request.publishedYear() != null){
            book.setPublishedYear(request.publishedYear());
        }

        if(request.totalCopies() != null){
            int difference = request.totalCopies() - book.getTotalCopies();
            book.setTotalCopies(request.totalCopies());
            book.setAvailableCopies(book.getAvailableCopies() + difference);
        }

        if(request.authorIds() != null && !request.authorIds().isEmpty()){
            List<Author> authors = authorRepository.findAllById(request.authorIds());
            if(authors.size()!=request.authorIds().size()){
                throw new AuthorNotFoundException("Author Ids do not match.");
            }
            book.setAuthors(new HashSet<>(authors));
        }

        Book updatedBook = bookRepository.save(book);
        return bookMapper.toResponse(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(Long id){
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new BookNotFoundException(
                                "Book not found with id: " + id));
        bookRepository.delete(book);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id){
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new BookNotFoundException(
                                "Book not found with id: " + id));
        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> findByTitle(String title, int page){
        Pageable pageable = PageRequest.of(page,pageSize, Sort.by("title").ascending());
        Page<Book> books = bookRepository.findByTitleContainingIgnoreCase(title,pageable);
        Page<BookResponse> response = books.map(bookMapper::toResponse);
        return new PageResponse<>(
                response.getContent(),
                response.getNumber(),
                response.getSize(),
                response.getTotalElements(),
                response.getTotalPages(),
                response.isFirst(),
                response.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> findByAuthors_NameContaining(int page, String authorName){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("title").ascending());
        Page<Book> books = bookRepository.findByAuthors_NameContaining(authorName,pageable);
        Page<BookResponse> response = books.map(bookMapper::toResponse);
        return new PageResponse<>(
                response.getContent(),
                response.getNumber(),
                response.getSize(),
                response.getTotalElements(),
                response.getTotalPages(),
                response.isFirst(),
                response.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> findByPublishedYearBetween(int page, Integer startYear, Integer endYear){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("title").ascending());
        Page<Book> books = bookRepository.findByPublishedYearBetween(startYear, endYear, pageable);
        Page<BookResponse> response = books.map(bookMapper::toResponse);
        return new PageResponse<>(
                response.getContent(),
                response.getNumber(),
                response.getSize(),
                response.getTotalElements(),
                response.getTotalPages(),
                response.isFirst(),
                response.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> findByGenre(int page, String genre) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("title").ascending());
        Page<Book> books = bookRepository.findByGenre(genre, pageable);
        Page<BookResponse> response = books.map(bookMapper::toResponse);
        return new PageResponse<>(
                response.getContent(),
                response.getNumber(),
                response.getSize(),
                response.getTotalElements(),
                response.getTotalPages(),
                response.isFirst(),
                response.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> findByStatus(BookStatus status, int page) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("title").ascending());
        Page<Book> books = bookRepository.findByStatus(status, pageable);
        Page<BookResponse> response = books.map(bookMapper::toResponse);
        return new PageResponse<>(
                response.getContent(),
                response.getNumber(),
                response.getSize(),
                response.getTotalElements(),
                response.getTotalPages(),
                response.isFirst(),
                response.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse findByIsbn(String isbn){
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book with ISBN " + isbn + " not found."));
        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getAllBooks(int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("title").ascending());
        Page<Book> books = bookRepository.findAll(pageable);
        Page<BookResponse> response = books.map(bookMapper::toResponse);
        return new PageResponse<>(
                response.getContent(),
                response.getNumber(),
                response.getSize(),
                response.getTotalElements(),
                response.getTotalPages(),
                response.isFirst(),
                response.isLast()
        );
    }
}
