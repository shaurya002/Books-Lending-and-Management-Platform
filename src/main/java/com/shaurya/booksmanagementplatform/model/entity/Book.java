package com.shaurya.booksmanagementplatform.model.entity;

import com.shaurya.booksmanagementplatform.model.enums.BookStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"authors", "borrowRecords"})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Title is mandatory")
    private String title;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "ISBN is mandatory")
    private String isbn;

    private String genre;

    private Integer publishedYear;

    @Column(nullable = false)
    @Positive(message = "Total copies must be positive")
    private Integer totalCopies;

    @Column(nullable = false)
    @PositiveOrZero(message = "Available copies must be zero or positive")
    private Integer availableCopies;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @Builder.Default
    private Set<Author> authors = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<BorrowRecord> borrowRecords = new HashSet<>();
}
