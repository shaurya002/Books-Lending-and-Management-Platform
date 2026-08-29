package com.shaurya.booksmanagementplatform.repositories;

import com.shaurya.booksmanagementplatform.model.entity.Book;
import com.shaurya.booksmanagementplatform.model.enums.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {

    Optional<Book> findByIsbn(String isbn);

    Page<Book> findByTitleContainingIgnoreCase(
            String title,
            Pageable pageable
    );

    Page<Book> findByGenre(String genre, Pageable pageable);

    Page<Book> findByStatus(BookStatus status, Pageable pageable);

    boolean existsByIsbn(String isbn);

    Page<Book> findByAuthors_NameContaining(String name, Pageable pageable);

    Page<Book> findByPublishedYearBetween(Integer startYear, Integer endYear, Pageable pageable);
}
