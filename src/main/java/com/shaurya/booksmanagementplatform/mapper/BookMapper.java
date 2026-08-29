package com.shaurya.booksmanagementplatform.mapper;

import com.shaurya.booksmanagementplatform.dto.request.BookRequest;
import com.shaurya.booksmanagementplatform.dto.response.BookResponse;
import com.shaurya.booksmanagementplatform.model.entity.Author;
import com.shaurya.booksmanagementplatform.model.entity.Book;
import com.shaurya.booksmanagementplatform.model.enums.BookStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class BookMapper {

    public Book toEntity(BookRequest request, Set<Author> authors){

        return Book.builder()
                .title(request.title())
                .isbn(request.isbn())
                .genre(request.genre())
                .publishedYear(request.publishedYear())
                .totalCopies(request.totalCopies())
                .availableCopies(request.totalCopies())
                .status(BookStatus.AVAILABLE)
                .authors(authors)
                .build();
    }

    public BookResponse toResponse(Book book){
        List<String> authors = book.getAuthors()
                .stream()
                .map(Author::getName)
                .toList();

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getGenre(),
                book.getPublishedYear(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                book.getStatus(),
                authors
        );
    }
}
