package com.shaurya.booksmanagementplatform.repositories;

import com.shaurya.booksmanagementplatform.model.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author,Long> {

    Optional<Author> findByName(String name);

    boolean existsByName(String name);

    Page<Author> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
