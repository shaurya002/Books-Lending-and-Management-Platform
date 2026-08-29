package com.shaurya.booksmanagementplatform.controller;

import com.shaurya.booksmanagementplatform.dto.request.BookRequest;
import com.shaurya.booksmanagementplatform.dto.response.BookResponse;
import com.shaurya.booksmanagementplatform.dto.response.PageResponse;
import com.shaurya.booksmanagementplatform.model.enums.BookStatus;
import com.shaurya.booksmanagementplatform.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Books",
        description = "APIs for managing books"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @Operation(
            summary = "Add a new book",
            description = "Creates a new book entry"
    )
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest bookRequest){
        BookResponse bookResponse = bookService.createBook(bookRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookResponse);
    }

    @Operation(
            summary = "Get all books",
            description = "Retrieves books with pagination sorted by title"
    )
    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> getAllBooks(@RequestParam(defaultValue = "0") int page){
        PageResponse<BookResponse> bookResponse = bookService.getAllBooks(page);
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookResponse);
    }

    @Operation(
            summary = "Get book by ID",
            description = "Retrieves a book by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id){
        BookResponse bookResponse = bookService.getBookById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookResponse);
    }

    @Operation(
            summary = "Search books by title",
            description = "Finds books by their title"
    )
    @GetMapping("/search/title")
    public ResponseEntity<PageResponse<BookResponse>> findByTitle(@RequestParam String title, @RequestParam(defaultValue = "0") int page){
        PageResponse<BookResponse> bookResponse = bookService.findByTitle(title, page);
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookResponse);
    }

    @Operation(
            summary = "Search books by author",
            description = "Finds books by their author's name"
    )
    @GetMapping("/search/author")
    public ResponseEntity<PageResponse<BookResponse>> findByAuthorName(@RequestParam String authorName, @RequestParam(defaultValue = "0") int page){
        PageResponse<BookResponse> bookResponse = bookService.findByAuthors_NameContaining(page, authorName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookResponse);
    }

    @Operation(
            summary = "Search books by publication year",
            description = "Finds books published within a specific year range"
    )
    @GetMapping("/search/year")
    public ResponseEntity<PageResponse<BookResponse>> findByPublishedYearBetween(@RequestParam(defaultValue = "0") int page, @RequestParam Integer startYear, @RequestParam Integer endYear){
        PageResponse<BookResponse> bookResponse = bookService.findByPublishedYearBetween(page, startYear, endYear);
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookResponse);
    }

    @Operation(
            summary = "Search books by genre",
            description = "Finds books by their genre"
    )
    @GetMapping("/search/genre")
    public ResponseEntity<PageResponse<BookResponse>> findByGenre(@RequestParam(defaultValue = "0") int page, @RequestParam String genre){
        PageResponse<BookResponse> bookResponse = bookService.findByGenre(page, genre);
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookResponse);
    }

    @Operation(
            summary = "Search books by status",
            description = "Finds books by their availability status"
    )
    @GetMapping("/search/status")
    public ResponseEntity<PageResponse<BookResponse>> findByStatus(@RequestParam(defaultValue = "0") int page, @RequestParam BookStatus status){
        PageResponse<BookResponse> bookResponse = bookService.findByStatus(status, page);
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookResponse);
    }

    @Operation(
            summary = "Update the book details",
            description = "Updates the details of an existing book"
    )
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest bookRequest){
        BookResponse bookResponse = bookService.updateBook(id, bookRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookResponse);
    }

    @Operation(
            summary = "Delete a book",
            description = "Removes a book from the library"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
