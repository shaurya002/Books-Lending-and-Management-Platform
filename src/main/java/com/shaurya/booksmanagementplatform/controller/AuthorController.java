package com.shaurya.booksmanagementplatform.controller;

import com.shaurya.booksmanagementplatform.dto.request.AuthorRequest;
import com.shaurya.booksmanagementplatform.dto.response.AuthorResponse;
import com.shaurya.booksmanagementplatform.dto.response.PageResponse;
import com.shaurya.booksmanagementplatform.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Authors",
        description = "APIs for managing authors"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    @Operation(
            summary = "Add a new author",
            description = "Creates a new author entry"
    )
    @PostMapping
    public ResponseEntity<AuthorResponse> addAuthor(@Valid @RequestBody AuthorRequest authorRequest){
        AuthorResponse authorResponse = authorService.createAuthor(authorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authorResponse);
    }

    @Operation(
            summary = "Get author by ID",
            description = "Retrieves an author by their ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthor(@PathVariable Long id){
        AuthorResponse authorResponse = authorService.getAuthorById(id);
        return ResponseEntity.status(HttpStatus.OK).body(authorResponse);
    }

    @Operation(
            summary = "Update author details",
            description = "Updates the details of an existing author"
    )
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorRequest authorRequest){
        AuthorResponse authorResponse = authorService.updateAuthor(id, authorRequest);
        return ResponseEntity.status(HttpStatus.OK).body(authorResponse);
    }

    @Operation(
            summary = "Delete an author",
            description = "Removes an author from the system"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id){
        authorService.deleteAuthor(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Search authors by name",
            description = "Finds authors by their name"
    )
    @GetMapping("/name")
    public ResponseEntity<PageResponse<AuthorResponse>> findAuthorsByName(@RequestParam String name, @RequestParam(defaultValue = "0") int page){
        PageResponse<AuthorResponse> authorResponses = authorService.findByName(name, page);
        return ResponseEntity.status(HttpStatus.OK).body(authorResponses);
    }

    @Operation(
            summary = "Get all authors",
            description = "Retrieves a paginated list of all authors"
    )
    @GetMapping
    public ResponseEntity<PageResponse<AuthorResponse>> getAllAuthors(@RequestParam(defaultValue = "0") int page){
        PageResponse<AuthorResponse> authorResponses = authorService.getAllAuthors(page);
        return ResponseEntity.status(HttpStatus.OK).body(authorResponses);
    }
}
