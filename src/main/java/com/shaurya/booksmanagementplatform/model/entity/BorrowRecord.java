package com.shaurya.booksmanagementplatform.model.entity;

import com.shaurya.booksmanagementplatform.model.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"book", "member"})
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate borrowDate;

    private LocalDate returnDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private Double fine;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BorrowStatus borrowStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
