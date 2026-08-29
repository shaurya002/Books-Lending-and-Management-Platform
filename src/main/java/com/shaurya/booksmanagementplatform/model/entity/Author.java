package com.shaurya.booksmanagementplatform.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"books"})
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Size(max = 2000)
    private String biography;

    @ToString.Exclude
    @ManyToMany(mappedBy = "authors")
    @Builder.Default
    private Set<Book> books = new HashSet<>();
}
